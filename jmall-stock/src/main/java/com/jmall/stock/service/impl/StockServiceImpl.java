package com.jmall.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jmall.api.stock.dto.StockDeductDTO;
import com.jmall.common.core.constant.RedisConstants;
import com.jmall.common.core.exception.BusinessException;
import com.jmall.common.core.result.ResultCode;
import com.jmall.common.redis.lock.DistributedLock;
import com.jmall.common.redis.utils.CacheUtils;
import com.jmall.stock.entity.Stock;
import com.jmall.stock.entity.StockRecord;
import com.jmall.stock.mapper.StockMapper;
import com.jmall.stock.mapper.StockRecordMapper;
import com.jmall.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 库存服务实现（DB 乐观锁 + Redis 缓存 + 幂等流水）
 *
 * @author jmall
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockMapper stockMapper;
    private final StockRecordMapper stockRecordMapper;
    private final CacheUtils cacheUtils;
    private final DistributedLock distributedLock;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deduct(StockDeductDTO dto) {
        if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "扣减明细不能为空");
        }
        String orderNo = dto.getOrderNo();
        // 幂等：同一订单已扣减则直接成功
        Long exists = stockRecordMapper.selectCount(
                new LambdaQueryWrapper<StockRecord>()
                        .eq(StockRecord::getOrderNo, orderNo)
                        .eq(StockRecord::getType, 2));
        if (exists != null && exists > 0) {
            log.info("库存扣减幂等命中: orderNo={}", orderNo);
            return true;
        }

        for (StockDeductDTO.StockItem item : dto.getItems()) {
            String lockKey = RedisConstants.STOCK_LOCK_KEY + item.getSkuId();
            distributedLock.executeWithLock(lockKey, 3L, 10L, () -> {
                Stock stock = getOrThrow(item.getSkuId());
                int need = item.getQuantity() == null ? 0 : item.getQuantity();
                if (need <= 0) {
                    throw new BusinessException(ResultCode.BAD_REQUEST, "扣减数量非法");
                }
                if (stock.getAvailableQuantity() < need) {
                    throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
                }
                int before = stock.getAvailableQuantity();
                stock.setLockedQuantity(stock.getLockedQuantity() + need);
                stock.setAvailableQuantity(stock.getQuantity() - stock.getLockedQuantity());
                int updated = stockMapper.updateById(stock);
                if (updated == 0) {
                    throw new BusinessException(ResultCode.STOCK_DEDUCT_FAIL, "库存并发更新失败");
                }

                StockRecord record = new StockRecord();
                record.setOrderNo(orderNo);
                record.setSkuId(item.getSkuId());
                record.setQuantity(-need);
                record.setType(2);
                record.setBeforeQuantity(before);
                record.setAfterQuantity(stock.getAvailableQuantity());
                record.setBizId(orderNo + ":" + item.getSkuId() + ":deduct");
                record.setCreateTime(LocalDateTime.now());
                stockRecordMapper.insert(record);

                // 刷新缓存
                cacheUtils.set(RedisConstants.STOCK_KEY + item.getSkuId(), stock.getAvailableQuantity(), 600L);
                return null;
            });
        }
        log.info("库存扣减成功: orderNo={}", orderNo);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rollback(String orderNo) {
        List<StockRecord> records = stockRecordMapper.selectList(
                new LambdaQueryWrapper<StockRecord>()
                        .eq(StockRecord::getOrderNo, orderNo)
                        .eq(StockRecord::getType, 2));
        if (records.isEmpty()) {
            log.info("无扣减流水可回滚: orderNo={}", orderNo);
            return true;
        }
        // 已回滚幂等
        Long rolled = stockRecordMapper.selectCount(
                new LambdaQueryWrapper<StockRecord>()
                        .eq(StockRecord::getOrderNo, orderNo)
                        .eq(StockRecord::getType, 3));
        if (rolled != null && rolled > 0) {
            return true;
        }

        for (StockRecord r : records) {
            String lockKey = RedisConstants.STOCK_LOCK_KEY + r.getSkuId();
            distributedLock.executeWithLock(lockKey, 3L, 10L, () -> {
                Stock stock = getOrThrow(r.getSkuId());
                int restore = Math.abs(r.getQuantity());
                int before = stock.getAvailableQuantity();
                stock.setLockedQuantity(Math.max(0, stock.getLockedQuantity() - restore));
                stock.setAvailableQuantity(stock.getQuantity() - stock.getLockedQuantity());
                int updated = stockMapper.updateById(stock);
                if (updated == 0) {
                    throw new BusinessException(ResultCode.STOCK_DEDUCT_FAIL, "库存回滚失败");
                }

                StockRecord back = new StockRecord();
                back.setOrderNo(orderNo);
                back.setSkuId(r.getSkuId());
                back.setQuantity(restore);
                back.setType(3);
                back.setBeforeQuantity(before);
                back.setAfterQuantity(stock.getAvailableQuantity());
                back.setBizId(orderNo + ":" + r.getSkuId() + ":rollback");
                back.setCreateTime(LocalDateTime.now());
                stockRecordMapper.insert(back);
                cacheUtils.set(RedisConstants.STOCK_KEY + r.getSkuId(), stock.getAvailableQuantity(), 600L);
                return null;
            });
        }
        log.info("库存回滚成功: orderNo={}", orderNo);
        return true;
    }

    @Override
    public Integer getAvailable(Long skuId) {
        Object cached = cacheUtils.get(RedisConstants.STOCK_KEY + skuId);
        if (cached instanceof Integer i) {
            return i;
        }
        if (cached instanceof Number n) {
            return n.intValue();
        }
        Stock stock = stockMapper.selectOne(new LambdaQueryWrapper<Stock>()
                .eq(Stock::getSkuId, skuId)
                .last("limit 1"));
        int available = stock == null ? 0 : stock.getAvailableQuantity();
        cacheUtils.set(RedisConstants.STOCK_KEY + skuId, available, 600L);
        return available;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setStock(Long skuId, Long merchantId, Integer quantity) {
        Stock stock = stockMapper.selectOne(new LambdaQueryWrapper<Stock>()
                .eq(Stock::getSkuId, skuId)
                .last("limit 1"));
        if (stock == null) {
            stock = new Stock();
            stock.setSkuId(skuId);
            stock.setMerchantId(merchantId);
            stock.setQuantity(quantity);
            stock.setLockedQuantity(0);
            stock.setAvailableQuantity(quantity);
            stock.setWarnStock(10);
            stock.setVersion(0);
            stock.setStatus(1);
            stockMapper.insert(stock);
        } else {
            stock.setQuantity(quantity);
            stock.setAvailableQuantity(quantity - stock.getLockedQuantity());
            stockMapper.updateById(stock);
        }
        cacheUtils.set(RedisConstants.STOCK_KEY + skuId, stock.getAvailableQuantity(), 600L);
    }

    private Stock getOrThrow(Long skuId) {
        Stock stock = stockMapper.selectOne(new LambdaQueryWrapper<Stock>()
                .eq(Stock::getSkuId, skuId)
                .last("limit 1"));
        if (stock == null || stock.getStatus() == null || stock.getStatus() != 1) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH, "SKU库存不存在或已停用");
        }
        return stock;
    }
}
