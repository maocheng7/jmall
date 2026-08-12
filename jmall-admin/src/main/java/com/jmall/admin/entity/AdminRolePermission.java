package com.jmall.admin.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data; import java.time.LocalDateTime;
@Data @TableName("admin_role_permission") public class AdminRolePermission { @TableId(type=IdType.ASSIGN_ID) private Long id; private Long roleId,permissionId; private LocalDateTime createTime; }
