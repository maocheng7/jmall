package com.jmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 规格名实体（对应表 spec）
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("spec")
public class Spec extends BaseEntity {

    /** 规格名称（颜色/内存等） */
    private String name;
    /** 排序号 */
    private Integer sort;
}
