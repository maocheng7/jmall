package com.jmall.search.mq;

import com.jmall.common.core.constant.MqConstants;
import com.jmall.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 商品上架消息消费者 → 同步 ES 索引
 *
 * @author jmall
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MqConstants.TOPIC_PRODUCT_UP,
        consumerGroup = MqConstants.GROUP_PRODUCT_UP
)
public class ProductUpConsumer implements RocketMQListener<Long> {

    private final SearchService searchService;

    @Override
    public void onMessage(Long spuId) {
        log.info("收到商品上架消息: spuId={}", spuId);
        searchService.indexProduct(spuId);
    }
}
