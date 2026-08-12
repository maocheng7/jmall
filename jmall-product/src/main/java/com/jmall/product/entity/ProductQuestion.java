package com.jmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商品问答实体（对应表 product_question）
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_question")
public class ProductQuestion extends BaseEntity {

    private Long spuId;
    private Long userId;
    private String question;
    private String answer;
    private Long answerUserId;
    private LocalDateTime answerTime;
    /** 状态：0=待回答，1=已回答，2=已屏蔽 */
    private Integer status;
}
