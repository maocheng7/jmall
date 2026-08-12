package com.jmall.cart.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车项（存 Redis Hash）
 *
 * @author jmall
 */
@Data
public class CartItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long skuId;
    private Long spuId;
    private String productName;
    private String specValue;
    private String imageUrl;
    private BigDecimal price;
    private Integer quantity;
    private Boolean checked;
    private Long merchantId;
}
