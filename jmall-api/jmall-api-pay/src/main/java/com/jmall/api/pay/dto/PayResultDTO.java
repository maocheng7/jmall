package com.jmall.api.pay.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付结果 DTO（服务间调用传输对象）
 *
 * @author jmall
 */
@Data
public class PayResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付流水号
     */
    private String payNo;

    /**
     * 支付方式（1=微信, 2=支付宝, 3=余额, 4=模拟）
     */
    private Integer payType;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付状态（0=待支付, 1=已支付, 2=已退款）
     */
    private Integer payStatus;

    /**
     * 支付时间
     */
    private String payTime;
}
