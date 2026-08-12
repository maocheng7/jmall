package com.jmall.common.mq.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 消息封装基类
 * <p>
 * 所有 MQ 消息体继承此类，统一包含消息ID、时间戳、业务数据。
 * </p>
 *
 * @param <T> 业务数据类型
 * @author jmall
 */
@Data
public class MessageWrapper<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息唯一ID
     */
    private String messageId;

    /**
     * 消息主题
     */
    private String topic;

    /**
     * 消息标签
     */
    private String tag;

    /**
     * 消息创建时间
     */
    private LocalDateTime createTime;

    /**
     * 业务数据
     */
    private T data;

    public MessageWrapper() {
        this.messageId = UUID.randomUUID().toString().replace("-", "");
        this.createTime = LocalDateTime.now();
    }

    public MessageWrapper(String topic, String tag, T data) {
        this();
        this.topic = topic;
        this.tag = tag;
        this.data = data;
    }
}
