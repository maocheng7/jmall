package com.jmall.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jmall.api.order.dto.OrderDTO;
import com.jmall.api.order.feign.RemoteOrderService;
import com.jmall.api.pay.dto.PayResultDTO;
import com.jmall.common.core.enums.PayTypeEnum;
import com.jmall.common.core.exception.BusinessException;
import com.jmall.common.core.result.Result;
import com.jmall.common.core.result.ResultCode;
import com.jmall.pay.entity.PayOrder;
import com.jmall.pay.entity.RefundRecord;
import com.jmall.pay.mapper.PayOrderMapper;
import com.jmall.pay.mapper.RefundRecordMapper;
import com.jmall.pay.service.PayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 支付服务实现（默认 mock 支付：创建即成功并回调订单）
 *
 * @author jmall
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

    private final PayOrderMapper payOrderMapper;
    private final RefundRecordMapper refundRecordMapper;
    private final RemoteOrderService remoteOrderService;

    @Value("${jmall.pay.mock-enabled:true}")
    private boolean mockEnabled;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultDTO createPayment(String orderNo, Integer payType, BigDecimal amount) {
        // 已有成功支付单则直接返回
        PayOrder exist = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getOrderNo, orderNo)
                .eq(PayOrder::getStatus, 1)
                .last("limit 1"));
        if (exist != null) {
            return toDTO(exist);
        }

        PayOrder payOrder = new PayOrder();
        payOrder.setPayNo(genNo("P"));
        payOrder.setOrderNo(orderNo);
        payOrder.setUserId(0L);
        payOrder.setPayType(payType != null ? payType : PayTypeEnum.MOCK.getCode());
        payOrder.setPayAmount(amount);
        payOrder.setFeeAmount(BigDecimal.ZERO);
        payOrder.setStatus(0);
        payOrder.setNotifyStatus(0);
        payOrder.setNotifyCount(0);
        payOrder.setExpireTime(LocalDateTime.now().plusMinutes(30));
        payOrderMapper.insert(payOrder);

        if (mockEnabled || PayTypeEnum.MOCK.getCode() == payOrder.getPayType()) {
            // 模拟支付成功
            markSuccess(payOrder);
            notifyOrderPaid(orderNo);
        }
        return toDTO(payOrder);
    }

    @Override
    public PayResultDTO getPayResult(String orderNo) {
        PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getOrderNo, orderNo)
                .orderByDesc(PayOrder::getCreateTime)
                .last("limit 1"));
        if (payOrder == null) {
            throw new BusinessException(ResultCode.PAY_ERROR, "支付单不存在");
        }
        return toDTO(payOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refund(String orderNo) {
        PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getOrderNo, orderNo)
                .eq(PayOrder::getStatus, 1)
                .last("limit 1"));
        if (payOrder == null) {
            throw new BusinessException(ResultCode.PAY_REFUND_ERROR, "无可退款支付单");
        }

        RefundRecord record = new RefundRecord();
        record.setRefundNo(genNo("R"));
        record.setPayNo(payOrder.getPayNo());
        record.setOrderNo(orderNo);
        record.setRefundAmount(payOrder.getPayAmount());
        record.setRefundType(0);
        record.setTradeRefundNo("MOCK_RF_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        record.setStatus(1);
        record.setReason("订单退款");
        record.setRefundTime(LocalDateTime.now());
        refundRecordMapper.insert(record);

        payOrderMapper.update(null, new LambdaUpdateWrapper<PayOrder>()
                .eq(PayOrder::getId, payOrder.getId())
                .set(PayOrder::getStatus, 4));
        log.info("退款成功(模拟): orderNo={}, refundNo={}", orderNo, record.getRefundNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultDTO payOrder(Long userId, String orderNo, Integer payType) {
        Result<OrderDTO> orderResult = remoteOrderService.getOrderByNo(orderNo);
        if (!orderResult.isSuccess() || orderResult.getData() == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        OrderDTO order = orderResult.getData();
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        if (order.getStatus() != null && order.getStatus() != 0) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "订单状态不可支付");
        }

        // 已支付幂等
        PayOrder exist = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getOrderNo, orderNo)
                .eq(PayOrder::getStatus, 1)
                .last("limit 1"));
        if (exist != null) {
            return toDTO(exist);
        }

        PayOrder payOrder = new PayOrder();
        payOrder.setPayNo(genNo("P"));
        payOrder.setOrderNo(orderNo);
        payOrder.setUserId(userId);
        payOrder.setPayType(payType != null ? payType : PayTypeEnum.MOCK.getCode());
        payOrder.setPayAmount(order.getPayAmount());
        payOrder.setFeeAmount(BigDecimal.ZERO);
        payOrder.setStatus(0);
        payOrder.setNotifyStatus(0);
        payOrder.setNotifyCount(0);
        payOrder.setExpireTime(LocalDateTime.now().plusMinutes(30));
        payOrderMapper.insert(payOrder);

        // 模拟渠道：直接成功
        markSuccess(payOrder);
        notifyOrderPaid(orderNo);
        return toDTO(payOrder);
    }

    private void markSuccess(PayOrder payOrder) {
        payOrder.setStatus(1);
        payOrder.setTradeNo("MOCK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        payOrder.setPayTime(LocalDateTime.now());
        payOrder.setNotifyStatus(1);
        payOrder.setNotifyCount(1);
        payOrder.setNotifyTime(LocalDateTime.now());
        payOrderMapper.updateById(payOrder);
    }

    private void notifyOrderPaid(String orderNo) {
        try {
            Result<Boolean> result = remoteOrderService.updateOrderPaid(orderNo);
            if (!result.isSuccess() || !Boolean.TRUE.equals(result.getData())) {
                log.error("支付回调订单失败: orderNo={}, msg={}", orderNo, result.getMessage());
                throw new BusinessException(ResultCode.PAY_CALLBACK_ERROR);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("支付回调异常: orderNo={}", orderNo, e);
            throw new BusinessException(ResultCode.PAY_CALLBACK_ERROR, e.getMessage());
        }
    }

    private PayResultDTO toDTO(PayOrder payOrder) {
        PayResultDTO dto = new PayResultDTO();
        dto.setOrderNo(payOrder.getOrderNo());
        dto.setPayNo(payOrder.getPayNo());
        dto.setPayType(payOrder.getPayType());
        dto.setPayAmount(payOrder.getPayAmount());
        dto.setPayStatus(payOrder.getStatus());
        if (payOrder.getPayTime() != null) {
            dto.setPayTime(payOrder.getPayTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return dto;
    }

    private String genNo(String prefix) {
        return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }
}
