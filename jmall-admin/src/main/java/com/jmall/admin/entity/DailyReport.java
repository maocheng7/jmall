package com.jmall.admin.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data; import java.time.*; import java.math.BigDecimal;
@Data @TableName("daily_report") public class DailyReport { @TableId(type=IdType.ASSIGN_ID) private Long id; private LocalDate reportDate; private BigDecimal gmv,refundAmount; private Integer orderCount,paidOrderCount,newUserCount,newMerchantCount,activeUserCount,productCount; private LocalDateTime createTime,updateTime; }
