package com.jmall.search.entity;

import com.jmall.common.es.base.BaseEsEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品 ES 文档
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductEsDoc extends BaseEsEntity {

    /** SPU ID */
    private Long spuId;
    /** 商品名称 */
    private String name;
    /** 副标题 */
    private String subtitle;
    /** 主图 */
    private String mainImage;
    /** 分类ID */
    private Long categoryId;
    /** 品牌ID */
    private Long brandId;
    /** 商家ID */
    private Long merchantId;
    /** 最低价 */
    private BigDecimal minPrice;
    /** 最高价 */
    private BigDecimal maxPrice;
    /** 销量 */
    private Integer sales;
    /** 搜索关键词 */
    private String searchKeyword;
    /** 状态：1=上架 */
    private Integer status;
}
