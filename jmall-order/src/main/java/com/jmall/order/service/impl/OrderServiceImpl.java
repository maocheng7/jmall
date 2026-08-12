package com.jmall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jmall.api.order.dto.OrderDTO;
import com.jmall.api.product.dto.SkuDTO;
import com.jmall.api.product.feign.RemoteProductService;
import com.jmall.api.stock.dto.StockDeductDTO;
import com.jmall.api.stock.feign.RemoteStockService;
import com.jmall.api.user.dto.UserAddrDTO;
import com.jmall.api.user.feign.RemoteUserService;
import com.jmall.common.core.constant.RedisConstants;
import com.jmall.common.core.enums.OrderStatusEnum;
import com.jmall.common.core.exception.BusinessException;
import com.jmall.common.core.result.PageResult;
import com.jmall.common.core.result.Result;
import com.jmall.common.core.result.ResultCode;
import com.jmall.common.redis.utils.CacheUtils;
import com.jmall.order.dto.OrderCreateDTO;
import com.jmall.order.entity.Order;
import com.jmall.order.entity.OrderItem;
import com.jmall.order.entity.OrderStatusRecord;
import com.jmall.order.mapper.OrderItemMapper;
import com.jmall.order.mapper.OrderMapper;
import com.jmall.order.mapper.OrderStatusRecordMapper;
import com.jmall.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 * <p>
 * 下单流程：查地址 → 查SKU → 生成订单 → 扣库存（失败回滚）→ 返回订单。
 * 本步使用本地事务 + Feign 补偿；Seata 可在后续增强。
 * </p>
 *
 * @author jmall
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderStatusRecordMapper statusRecordMapper;
    private final RemoteUserService remoteUserService;
    private final RemoteProductService remoteProductService;
    private final RemoteStockService remoteStockService;
    private final CacheUtils cacheUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order create(Long userId, OrderCreateDTO dto) {
        // 1. 地址
        Result<List<UserAddrDTO>> addrResult = remoteUserService.listUserAddress(userId);
        if (!addrResult.isSuccess() || addrResult.getData() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "查询地址失败");
        }
        UserAddrDTO address = addrResult.getData().stream()
                .filter(a -> dto.getAddressId().equals(a.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResultCode.BAD_REQUEST, "收货地址不存在"));

        // 2. 批量查 SKU
        List<Long> skuIds = dto.getItems().stream().map(OrderCreateDTO.Item::getSkuId).toList();
        Result<List<SkuDTO>> skuResult = remoteProductService.listSkuByIds(skuIds);
        if (!skuResult.isSuccess() || skuResult.getData() == null || skuResult.getData().isEmpty()) {
            throw new BusinessException(ResultCode.SKU_NOT_FOUND);
        }
        Map<Long, SkuDTO> skuMap = skuResult.getData().stream()
                .collect(Collectors.toMap(SkuDTO::getSkuId, s -> s, (a, b) -> a));

        // 3. 校验可售 & 汇总金额（同商家简化：取第一个 SKU 的 merchantId）
        BigDecimal total = BigDecimal.ZERO;
        Long merchantId = null;
        List<OrderItem> items = new ArrayList<>();
        for (OrderCreateDTO.Item line : dto.getItems()) {
            SkuDTO sku = skuMap.get(line.getSkuId());
            if (sku == null) {
                throw new BusinessException(ResultCode.SKU_NOT_FOUND);
            }
            Result<Boolean> onSale = remoteProductService.checkSkuOnSale(line.getSkuId());
            if (!onSale.isSuccess() || !Boolean.TRUE.equals(onSale.getData())) {
                throw new BusinessException(ResultCode.PRODUCT_OFF_SHELF);
            }
            if (merchantId == null) {
                merchantId = sku.getMerchantId();
            }
            BigDecimal lineAmount = sku.getPrice().multiply(BigDecimal.valueOf(line.getQuantity()));
            total = total.add(lineAmount);

            OrderItem item = new OrderItem();
            item.setUserId(userId);
            item.setSpuId(sku.getSpuId());
            item.setSkuId(sku.getSkuId());
            item.setProductName(sku.getProductName());
            item.setSkuSpec(sku.getSpecValue());
            item.setProductImage(sku.getImageUrl());
            item.setPrice(sku.getPrice());
            item.setQuantity(line.getQuantity());
            item.setTotalAmount(lineAmount);
            item.setCommentStatus(0);
            items.add(item);
        }

        // 4. 生成订单号
        String orderNo = generateOrderNo();

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setMerchantId(merchantId != null ? merchantId : 0L);
        order.setStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
        order.setTotalAmount(total);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setPayAmount(total);
        order.setPayType(dto.getPayType());
        order.setUserCouponId(dto.getUserCouponId());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        order.setRemark(dto.getRemark());
        order.setSource(1);
        order.setIsSeckill(0);
        order.setCommentStatus(0);
        orderMapper.insert(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            item.setOrderNo(orderNo);
            orderItemMapper.insert(item);
        }
        saveStatus(orderNo, null, OrderStatusEnum.PENDING_PAYMENT.getCode(), "创建订单", "USER");

        // 5. 扣库存（失败则抛异常回滚订单本地事务）
        StockDeductDTO stockDto = new StockDeductDTO();
        stockDto.setOrderNo(orderNo);
        List<StockDeductDTO.StockItem> stockItems = new ArrayList<>();
        for (OrderCreateDTO.Item line : dto.getItems()) {
            StockDeductDTO.StockItem si = new StockDeductDTO.StockItem();
            si.setSkuId(line.getSkuId());
            si.setQuantity(line.getQuantity());
            stockItems.add(si);
        }
        stockDto.setItems(stockItems);
        Result<Boolean> stockResult = remoteStockService.deductStock(stockDto);
        if (!stockResult.isSuccess() || !Boolean.TRUE.equals(stockResult.getData())) {
            throw new BusinessException(ResultCode.ORDER_STOCK_NOT_ENOUGH,
                    stockResult.getMessage() != null ? stockResult.getMessage() : "库存不足");
        }

        log.info("下单成功: orderNo={}, userId={}, amount={}", orderNo, userId, order.getPayAmount());
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, String orderNo, String reason) {
        Order order = getByOrderNo(orderNo);
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权操作该订单");
        }
        if (order.getStatus() != OrderStatusEnum.PENDING_PAYMENT.getCode()) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        doCancel(order, reason, "USER");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markPaid(String orderNo) {
        Order order = getByOrderNo(orderNo);
        if (order.getStatus() == OrderStatusEnum.PENDING_SHIPMENT.getCode()) {
            return true;
        }
        if (order.getStatus() != OrderStatusEnum.PENDING_PAYMENT.getCode()) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        int from = order.getStatus();
        orderMapper.update(null, new LambdaUpdateWrapper<Order>()
                .eq(Order::getOrderNo, orderNo)
                .set(Order::getStatus, OrderStatusEnum.PENDING_SHIPMENT.getCode())
                .set(Order::getPayTime, LocalDateTime.now()));
        saveStatus(orderNo, from, OrderStatusEnum.PENDING_SHIPMENT.getCode(), "支付成功", "SYSTEM");
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markCancelled(String orderNo) {
        Order order = getByOrderNo(orderNo);
        if (order.getStatus() == OrderStatusEnum.CANCELLED.getCode()) {
            return true;
        }
        doCancel(order, "系统取消", "SYSTEM");
        return true;
    }

    @Override
    public Order getByOrderNo(String orderNo) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo)
                .last("limit 1"));
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        return order;
    }

    @Override
    public PageResult<Order> pageByUser(Long userId, Integer status, int page, int size) {
        Page<Order> p = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> result = orderMapper.selectPage(p, wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUserId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPayAmount(order.getPayAmount());
        dto.setPayType(order.getPayType());
        dto.setMerchantId(order.getMerchantId());
        return dto;
    }

    private void doCancel(Order order, String reason, String operator) {
        int from = order.getStatus();
        orderMapper.update(null, new LambdaUpdateWrapper<Order>()
                .eq(Order::getOrderNo, order.getOrderNo())
                .set(Order::getStatus, OrderStatusEnum.CANCELLED.getCode())
                .set(Order::getCancelReason, reason)
                .set(Order::getCancelTime, LocalDateTime.now()));
        saveStatus(order.getOrderNo(), from, OrderStatusEnum.CANCELLED.getCode(), reason, operator);

        Result<Boolean> rollback = remoteStockService.rollbackStock(order.getOrderNo());
        if (!rollback.isSuccess()) {
            log.error("库存回滚失败: orderNo={}, msg={}", order.getOrderNo(), rollback.getMessage());
            throw new BusinessException(ResultCode.ORDER_CANCEL_ERROR, "取消订单失败：库存回滚异常");
        }
        log.info("订单取消成功: orderNo={}", order.getOrderNo());
    }

    private void saveStatus(String orderNo, Integer from, int to, String remark, String operator) {
        OrderStatusRecord record = new OrderStatusRecord();
        record.setOrderNo(orderNo);
        record.setFromStatus(from);
        record.setToStatus(to);
        record.setRemark(remark);
        record.setOperator(operator);
        record.setCreateTime(LocalDateTime.now());
        statusRecordMapper.insert(record);
    }

    private String generateOrderNo() {
        String day = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String seqKey = RedisConstants.ORDER_SEQ_KEY + day;
        Long seq = cacheUtils.increment(seqKey, 1L);
        if (seq != null && seq == 1L) {
            cacheUtils.expire(seqKey, 2 * 24 * 3600L);
        }
        return "O" + day + String.format("%06d", seq == null ? 1 : seq);
    }
}
