package com.jmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品 SPU 实体（对应表 product_spu）
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_spu")
public class ProductSpu extends BaseEntity {

    /** 商品编号 */
    private String spuNo;
    /** 商家ID */
    private Long merchantId;
    /** 店铺ID */
    private Long shopId;
    /** 三级分类ID */
    private Long categoryId;
    /** 品牌ID */
    private Long brandId;
    /** 商品名称 */
    private String name;
    /** 副标题 */
    private String subtitle;
    /** 主图 */
    private String mainImage;
    /** 轮播图 JSON */
    private String images;
    /** 详情富文本 */
    private String detail;
    /** 搜索关键词 */
    private String searchKeyword;
    /** 销量 */
    private Integer sales;
    /** 上下架：0=下架，1=上架 */
    private Integer status;
    /** 审核状态：0=待审，1=通过，2=驳回 */
    private Integer auditStatus;
}
