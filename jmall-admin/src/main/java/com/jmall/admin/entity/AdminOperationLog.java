package com.jmall.admin.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data; import java.time.LocalDateTime;
@Data @TableName("admin_operation_log") public class AdminOperationLog { @TableId(type=IdType.ASSIGN_ID) private Long id; private Long adminId,duration; private String adminName,module,operation,method,requestUrl,requestParams,ip,errorMsg; private Integer isSuccess; private LocalDateTime createTime; }
