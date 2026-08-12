package com.jmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品 SKU 实体（对应表 product_sku）
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_sku")
public class ProductSku extends BaseEntity {

    /** 所属 SPU ID */
    private Long spuId;
    /** SKU 编码 */
    private String skuNo;
    /** 规格名称 */
    private String name;
    /** 规格值描述 */
    private String specValue;
    /** 规格图片 */
    private String image;
    /** 销售价 */
    private BigDecimal price;
    /** 原价 */
    private BigDecimal originalPrice;
    /** 成本价 */
    private BigDecimal costPrice;
    /** 重量 kg */
    private BigDecimal weight;
    /** 状态：0=禁用，1=启用 */
    private Integer status;
}
