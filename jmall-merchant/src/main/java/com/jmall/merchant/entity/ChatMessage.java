package com.jmall.merchant.entity;
import com.baomidou.mybatisplus.annotation.TableName; import com.jmall.common.mybatis.base.BaseEntity; import lombok.Data; import lombok.EqualsAndHashCode;
@Data @EqualsAndHashCode(callSuper=true) @TableName("chat_message")
public class ChatMessage extends BaseEntity { private Long sessionId; private Integer senderType; private Long senderId; private Integer contentType; private String content; private Integer isRead; }
