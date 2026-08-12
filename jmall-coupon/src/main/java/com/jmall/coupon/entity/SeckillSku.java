package com.jmall.coupon.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data; import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
@Data @EqualsAndHashCode(callSuper=true) @TableName("seckill_sku")
public class SeckillSku extends BaseEntity { private Long activityId,spuId,skuId; private BigDecimal seckillPrice; private Integer seckillStock,soldCount,limitPerUser,status; }
