package com.jmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 品牌实体（对应表 brand）
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("brand")
public class Brand extends BaseEntity {

    /** 品牌名称 */
    private String name;
    /** Logo URL */
    private String logo;
    /** 品牌描述 */
    private String description;
    /** 首字母 */
    private String letter;
    /** 排序号 */
    private Integer sort;
    /** 状态：0=停用，1=启用 */
    private Integer status;
}
