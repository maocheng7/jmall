package com.jmall.coupon.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data; import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
@Data @EqualsAndHashCode(callSuper=true) @TableName("seckill_activity")
public class SeckillActivity extends BaseEntity { private String activityName; private LocalDateTime startTime,endTime; private Integer limitPerUser,status; }
