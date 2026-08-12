package com.jmall.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant")
public class Merchant extends BaseEntity {
    private Long userId;
    private String name;
    private String phone;
    private String logo;
    private Integer status;
    private LocalDateTime approveTime;
    private String approveRemark;
}
