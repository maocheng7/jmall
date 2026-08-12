package com.jmall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户浏览足迹实体（对应表 user_footprint）
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_footprint")
public class UserFootprint extends BaseEntity {

    /** 用户ID */
    private Long userId;
    /** 商品SPU ID */
    private Long spuId;
    /** SKU ID */
    private Long skuId;
}
