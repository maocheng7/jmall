package com.jmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类实体（对应表 category）
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity {

    /** 父分类ID（0=顶级） */
    private Long parentId;
    /** 分类名称 */
    private String name;
    /** 层级：1/2/3 */
    private Integer level;
    /** 排序号 */
    private Integer sort;
    /** 分类图标 */
    private String icon;
    /** 分类图片 */
    private String image;
    /** 状态：0=停用，1=启用 */
    private Integer status;
}
