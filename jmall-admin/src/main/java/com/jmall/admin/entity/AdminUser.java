package com.jmall.admin.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data; import java.time.LocalDateTime;
@Data @TableName("admin_user") public class AdminUser { @TableId(type=IdType.ASSIGN_ID) private Long id; private String username,password,name,avatar,phone,email; private Integer status; private LocalDateTime lastLoginTime,createTime,updateTime; private Integer deleted; }
