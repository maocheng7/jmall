package com.jmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 规格值实体（对应表 spec_value）
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("spec_value")
public class SpecValue extends BaseEntity {

    /** 所属规格名ID */
    private Long specId;
    /** 规格值 */
    private String value;
    /** 排序号 */
    private Integer sort;
}
