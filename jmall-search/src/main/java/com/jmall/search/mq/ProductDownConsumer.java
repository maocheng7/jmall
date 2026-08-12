package com.jmall.search.mq;

import com.jmall.common.core.constant.MqConstants;
import com.jmall.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 商品下架消息消费者 → 从 ES 删除
 *
 * @author jmall
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MqConstants.TOPIC_PRODUCT_DOWN,
        consumerGroup = MqConstants.GROUP_PRODUCT_DOWN
)
public class ProductDownConsumer implements RocketMQListener<Long> {

    private final SearchService searchService;

    @Override
    public void onMessage(Long spuId) {
        log.info("收到商品下架消息: spuId={}", spuId);
        searchService.deleteProduct(spuId);
    }
}
