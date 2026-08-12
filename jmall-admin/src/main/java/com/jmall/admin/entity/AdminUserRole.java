package com.jmall.admin.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data; import java.time.LocalDateTime;
@Data @TableName("admin_user_role") public class AdminUserRole { @TableId(type=IdType.ASSIGN_ID) private Long id; private Long userId,roleId; private LocalDateTime createTime; }
