package com.jmall.common.mq.producer;

import com.jmall.common.core.exception.SystemException;
import com.jmall.common.mq.model.MessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;

/**
 * 消息生产者基类
 * <p>
 * 所有需要发送 MQ 消息的服务继承此类，复用发送逻辑。
 * 子类需注入 RocketMQTemplate 并指定 Topic。
 * </p>
 *
 * @author jmall
 */
@Slf4j
public abstract class BaseMessageProducer {

    /**
     * 获取 RocketMQTemplate（由子类提供）
     */
    protected abstract RocketMQTemplate getRocketMQTemplate();

    /**
     * 同步发送消息
     *
     * @param topic   主题
     * @param message 消息体
     */
    public void syncSend(String topic, Object message) {
        try {
            getRocketMQTemplate().syncSend(topic, MessageBuilder.withPayload(message).build());
            log.info("MQ同步发送成功: topic={}", topic);
        } catch (Exception e) {
            log.error("MQ同步发送失败: topic={}, error={}", topic, e.getMessage(), e);
            throw new SystemException("MQ消息发送失败: " + topic, e);
        }
    }

    /**
     * 同步发送消息（带标签）
     *
     * @param topic   主题
     * @param tag     标签
     * @param message 消息体
     */
    public void syncSend(String topic, String tag, Object message) {
        String destination = topic + ":" + tag;
        syncSend(destination, message);
    }

    /**
     * 异步发送消息
     *
     * @param topic   主题
     * @param message 消息体
     */
    public void asyncSend(String topic, Object message) {
        try {
            getRocketMQTemplate().asyncSend(topic, MessageBuilder.withPayload(message).build(),
                    new org.apache.rocketmq.client.producer.SendCallback() {
                        @Override
                        public void onSuccess(org.apache.rocketmq.client.producer.SendResult sendResult) {
                            log.info("MQ异步发送成功: topic={}, msgId={}", topic, sendResult.getMsgId());
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.error("MQ异步发送失败: topic={}, error={}", topic, e.getMessage(), e);
                        }
                    });
        } catch (Exception e) {
            log.error("MQ异步发送异常: topic={}, error={}", topic, e.getMessage(), e);
            throw new SystemException("MQ消息异步发送失败: " + topic, e);
        }
    }

    /**
     * 发送延时消息
     *
     * @param topic        主题
     * @param message      消息体
     * @param delayLevel   延时级别（1=1s, 2=5s, 3=10s, 4=30s, 5=1m, ...）
     */
    public void syncSendDelay(String topic, Object message, int delayLevel) {
        try {
            getRocketMQTemplate().syncSend(topic,
                    MessageBuilder.withPayload(message).build(),
                    3000,
                    delayLevel);
            log.info("MQ延时消息发送成功: topic={}, delayLevel={}", topic, delayLevel);
        } catch (Exception e) {
            log.error("MQ延时消息发送失败: topic={}, error={}", topic, e.getMessage(), e);
            throw new SystemException("MQ延时消息发送失败: " + topic, e);
        }
    }

    /**
     * 发送单向消息（不等待应答，性能最高）
     *
     * @param topic   主题
     * @param message 消息体
     */
    public void sendOneWay(String topic, Object message) {
        try {
            getRocketMQTemplate().sendOneWay(topic, MessageBuilder.withPayload(message).build());
            log.info("MQ单向消息发送成功: topic={}", topic);
        } catch (Exception e) {
            log.error("MQ单向消息发送失败: topic={}, error={}", topic, e.getMessage(), e);
            throw new SystemException("MQ单向消息发送失败: " + topic, e);
        }
    }

    /**
     * 发送封装消息
     *
     * @param wrapper 消息封装
     */
    public <T> void syncSendWrapper(MessageWrapper<T> wrapper) {
        syncSend(wrapper.getTopic() + ":" + wrapper.getTag(), wrapper.getData());
    }
}
