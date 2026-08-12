package com.jmall.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表实体（表名 orders）
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("orders")
public class Order extends BaseEntity {

    private String orderNo;
    private Long userId;
    private Long merchantId;
    private Long shopId;
    private Integer status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal freightAmount;
    private BigDecimal payAmount;
    private Integer payType;
    private LocalDateTime payTime;
    private Long userCouponId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private Integer source;
    private Integer isSeckill;
    private String cancelReason;
    private LocalDateTime cancelTime;
    private Integer commentStatus;
    private String deliveryNo;
}
