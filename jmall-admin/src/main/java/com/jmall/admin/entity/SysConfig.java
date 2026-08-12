package com.jmall.admin.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data; import java.time.LocalDateTime;
@Data @TableName("sys_config") public class SysConfig { @TableId(type=IdType.ASSIGN_ID) private Long id; private String configKey,configValue,configType,description; private Integer status,deleted; private LocalDateTime createTime,updateTime; }
