package com.jmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商品评价实体（对应表 product_review）
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_review")
public class ProductReview extends BaseEntity {

    private String orderNo;
    private Long orderItemId;
    private Long userId;
    private Long spuId;
    private Long skuId;
    /** 评分 1-5 */
    private Integer score;
    private String content;
    /** 评价图片 JSON */
    private String images;
    private String reply;
    private LocalDateTime replyTime;
    private Integer isAnonymous;
    private Integer isTop;
    /** 状态：0=隐藏，1=显示 */
    private Integer status;
}
