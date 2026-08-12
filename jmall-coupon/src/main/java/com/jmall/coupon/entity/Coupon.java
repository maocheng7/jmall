package com.jmall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("coupon")
public class Coupon extends BaseEntity {
    private Long merchantId; private String name; private Integer type;
    private BigDecimal discountAmount, discountRate, minAmount;
    private Integer totalCount, issuedCount, usedCount, perUserLimit;
    private LocalDateTime startTime, endTime, validStartTime, validEndTime;
    private Integer validDays, scopeType, status; private String scopeJson;
}
