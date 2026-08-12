package com.jmall.api.message.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 消息发送 DTO（服务间调用传输对象）
 *
 * @author jmall
 */
@Data
public class MessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息类型（sms=短信, site=站内信, push=推送）
     */
    private String type;

    /**
     * 接收者（手机号/用户ID）
     */
    private String receiver;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板参数
     */
    private Map<String, String> params;

    /**
     * 业务标识（如订单号，用于幂等控制）
     */
    private String bizId;
}
