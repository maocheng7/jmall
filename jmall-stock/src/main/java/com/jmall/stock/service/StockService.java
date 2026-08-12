package com.jmall.stock.service;

import com.jmall.api.stock.dto.StockDeductDTO;

/**
 * 库存服务
 *
 * @author jmall
 */
public interface StockService {

    /** 扣减/锁定库存 */
    boolean deduct(StockDeductDTO dto);

    /** 按订单回滚库存 */
    boolean rollback(String orderNo);

    /** 查询可用库存 */
    Integer getAvailable(Long skuId);

    /** 初始化/设置库存 */
    void setStock(Long skuId, Long merchantId, Integer quantity);
}
