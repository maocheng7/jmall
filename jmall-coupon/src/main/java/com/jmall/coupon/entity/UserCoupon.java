package com.jmall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_coupon")
public class UserCoupon extends BaseEntity {
    private Long userId; private Long couponId; private String couponName; private Integer type;
    private BigDecimal discountAmount, discountRate, minAmount; private Integer status; private String orderNo;
    private LocalDateTime useTime, expireTime, receivedTime;
}
