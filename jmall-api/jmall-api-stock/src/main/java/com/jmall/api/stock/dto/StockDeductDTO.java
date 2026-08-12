package com.jmall.api.stock.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 库存扣减 DTO（服务间调用传输对象）
 *
 * @author jmall
 */
@Data
public class StockDeductDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 库存扣减明细列表
     */
    private List<StockItem> items;

    /**
     * 库存扣减明细
     */
    @Data
    public static class StockItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * SKU ID
         */
        private Long skuId;

        /**
         * 扣减数量
         */
        private Integer quantity;
    }
}
