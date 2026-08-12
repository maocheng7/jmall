package com.jmall.admin.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data; import java.time.*;
@Data @TableName("admin_permission") public class AdminPermission { @TableId(type=IdType.ASSIGN_ID) private Long id; private Long parentId; private String permName; private Integer permType; private String permCode,path,icon; private Integer sort,status,deleted; private LocalDateTime createTime,updateTime; }
