package com.jmall.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款流水
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("refund_record")
public class RefundRecord extends BaseEntity {

    private String refundNo;
    private String payNo;
    private String orderNo;
    private BigDecimal refundAmount;
    private Integer refundType;
    private String tradeRefundNo;
    private Integer status;
    private String reason;
    private LocalDateTime refundTime;
}
