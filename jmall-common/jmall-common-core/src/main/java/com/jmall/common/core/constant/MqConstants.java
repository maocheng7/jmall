package com.jmall.common.core.constant;

/**
 * MQ Topic / Tag 常量定义
 * <p>
 * 统一管理所有 RocketMQ 消息主题和标签。命名规范：Topic 大写驼峰，Tag 小写连字符。
 * </p>
 *
 * @author jmall
 */
public final class MqConstants {

    private MqConstants() {
    }

    /* ==================== 订单相关 ==================== */
    /** 订单创建 Topic */
    public static final String TOPIC_ORDER_CREATE = "jmall-order-create";
    /** 订单创建 - 延时取消（Tag） */
    public static final String TAG_ORDER_DELAY_CANCEL = "delay-cancel";
    /** 订单支付成功 Topic */
    public static final String TOPIC_ORDER_PAID = "jmall-order-paid";
    /** 订单取消 Topic */
    public static final String TOPIC_ORDER_CANCEL = "jmall-order-cancel";

    /* ==================== 库存相关 ==================== */
    /** 库存扣减 Topic */
    public static final String TOPIC_STOCK_DEDUCT = "jmall-stock-deduct";
    /** 库存回滚 Topic */
    public static final String TOPIC_STOCK_ROLLBACK = "jmall-stock-rollback";

    /* ==================== 商品相关 ==================== */
    /** 商品上架 Topic（同步到ES） */
    public static final String TOPIC_PRODUCT_UP = "jmall-product-up";
    /** 商品下架 Topic（从ES删除） */
    public static final String TOPIC_PRODUCT_DOWN = "jmall-product-down";

    /* ==================== 秒杀相关 ==================== */
    /** 秒杀订单 Topic */
    public static final String TOPIC_SECKILL_ORDER = "jmall-seckill-order";

    /* ==================== 消息相关 ==================== */
    /** 短信发送 Topic */
    public static final String TOPIC_SMS_SEND = "jmall-sms-send";
    /** 站内信 Topic */
    public static final String TOPIC_SITE_MESSAGE = "jmall-site-message";

    /* ==================== 支付相关 ==================== */
    /** 支付成功 Topic */
    public static final String TOPIC_PAY_SUCCESS = "jmall-pay-success";
    /** 退款 Topic */
    public static final String TOPIC_PAY_REFUND = "jmall-pay-refund";

    /* ==================== 消费者组 ==================== */
    public static final String GROUP_ORDER_CREATE = "jmall-order-create-group";
    public static final String GROUP_ORDER_PAID = "jmall-order-paid-group";
    public static final String GROUP_ORDER_CANCEL = "jmall-order-cancel-group";
    public static final String GROUP_STOCK_DEDUCT = "jmall-stock-deduct-group";
    public static final String GROUP_STOCK_ROLLBACK = "jmall-stock-rollback-group";
    public static final String GROUP_PRODUCT_UP = "jmall-product-up-group";
    public static final String GROUP_PRODUCT_DOWN = "jmall-product-down-group";
    public static final String GROUP_SECKILL_ORDER = "jmall-seckill-order-group";
    public static final String GROUP_SMS_SEND = "jmall-sms-send-group";
    public static final String GROUP_SITE_MESSAGE = "jmall-site-message-group";
    public static final String GROUP_PAY_SUCCESS = "jmall-pay-success-group";
    public static final String GROUP_PAY_REFUND = "jmall-pay-refund-group";
}
