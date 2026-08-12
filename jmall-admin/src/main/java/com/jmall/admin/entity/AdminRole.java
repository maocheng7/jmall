package com.jmall.admin.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data; import java.time.LocalDateTime;
@Data @TableName("admin_role") public class AdminRole { @TableId(type=IdType.ASSIGN_ID) Long id; String roleName,roleCode,description; Integer status,deleted; LocalDateTime createTime,updateTime; }
