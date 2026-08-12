package com.jmall.coupon.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data; import lombok.EqualsAndHashCode;
import java.math.BigDecimal; import java.time.LocalDateTime;
@Data @EqualsAndHashCode(callSuper=true) @TableName("promotion")
public class Promotion extends BaseEntity { private Long merchantId; private String name; private Integer type; private BigDecimal thresholdAmount,discountAmount,discountRate; private Integer scopeType; private String scopeIds; private LocalDateTime startTime,endTime; private Integer status; }
