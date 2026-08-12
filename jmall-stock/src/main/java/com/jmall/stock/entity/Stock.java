package com.jmall.stock.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存实体
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stock")
public class Stock extends BaseEntity {

    private Long skuId;
    private Long merchantId;
    private Integer quantity;
    private Integer lockedQuantity;
    private Integer availableQuantity;
    private Integer warnStock;
    @Version
    private Integer version;
    private Integer status;
}
