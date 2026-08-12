package com.jmall.api.product.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品 SKU DTO（服务间调用传输对象）
 *
 * @author jmall
 */
@Data
public class SkuDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 规格值（如 "红色,128GB"）
     */
    private String specValue;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 状态（0=下架, 1=上架）
     */
    private Integer status;
}
