package com.jmall.api.coupon.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 优惠券 DTO（服务间调用传输对象）
 *
 * @author jmall
 */
@Data
public class CouponDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型（1=满减, 2=折扣, 3=无门槛）
     */
    private Integer type;

    /**
     * 优惠金额（满减/无门槛）
     */
    private BigDecimal discountAmount;

    /**
     * 使用门槛（满多少可用）
     */
    private BigDecimal minAmount;

    /**
     * 折扣率（折扣类型，如 0.85 表示85折）
     */
    private BigDecimal discountRate;
}
