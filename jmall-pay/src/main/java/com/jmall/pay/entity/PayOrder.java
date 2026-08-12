package com.jmall.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付单
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pay_order")
public class PayOrder extends BaseEntity {

    private String payNo;
    private String orderNo;
    private Long userId;
    private Integer payType;
    private BigDecimal payAmount;
    private BigDecimal feeAmount;
    private String tradeNo;
    private Integer status;
    private Integer notifyStatus;
    private Integer notifyCount;
    private LocalDateTime notifyTime;
    private LocalDateTime expireTime;
    private LocalDateTime payTime;
    private String clientIp;
}
