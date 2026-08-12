package com.jmall.admin.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data; import java.time.LocalDateTime;
@Data @TableName("home_banner") public class HomeBanner { @TableId(type=IdType.ASSIGN_ID) private Long id; private String title,imageUrl,linkUrl; private Integer position,sort,status,deleted; private LocalDateTime startTime,endTime,createTime,updateTime; }
