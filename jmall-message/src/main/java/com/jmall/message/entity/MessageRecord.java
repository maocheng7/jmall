package com.jmall.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("message_record")
public class MessageRecord extends BaseEntity {
    private String type;
    private String receiver;
    private Long userId;
    private String templateCode;
    private String params;
    private String content;
    private String bizId;
    private Integer status;
    private String errorMsg;
    private LocalDateTime sendTime;
}
