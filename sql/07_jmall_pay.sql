-- ============================================================
-- jmall_pay 支付服务库
-- 表：pay_order 支付单 / refund_record 退款流水
-- ============================================================

USE `jmall_pay`;

-- ------------------------------------------------------------
-- 1. 支付单表（一订单可对应多笔支付单：主支付 + 部分退款）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `pay_order`;
CREATE TABLE `pay_order` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `pay_no`        VARCHAR(32)   NOT NULL COMMENT '支付单号（业务唯一）',
    `order_no`      VARCHAR(32)   NOT NULL COMMENT '订单号（逻辑外键 jmall_order.orders.order_no）',
    `user_id`       BIGINT        NOT NULL COMMENT '用户ID',
    `pay_type`      TINYINT       NOT NULL COMMENT '支付方式：1=微信，2=支付宝，3=余额，4=模拟',
    `pay_amount`    DECIMAL(12,2) NOT NULL COMMENT '支付金额（元）',
    `fee_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '手续费（元）',
    `trade_no`      VARCHAR(64)   DEFAULT NULL COMMENT '第三方交易流水号',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '支付状态：0=待支付，1=支付成功，2=支付失败，3=已关闭，4=已退款',
    `notify_status` TINYINT       NOT NULL DEFAULT 0 COMMENT '回调通知状态：0=未通知，1=通知成功，2=通知失败',
    `notify_count`  INT           NOT NULL DEFAULT 0 COMMENT '回调通知次数',
    `notify_time`   DATETIME      DEFAULT NULL COMMENT '最后通知时间',
    `expire_time`   DATETIME      DEFAULT NULL COMMENT '支付过期时间（超时关闭）',
    `pay_time`      DATETIME      DEFAULT NULL COMMENT '支付成功时间',
    `client_ip`     VARCHAR(64)   DEFAULT NULL COMMENT '下单客户端IP',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_pay_no` (`pay_no`, `deleted`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_trade_no` (`trade_no`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付单表';

-- ------------------------------------------------------------
-- 2. 退款流水表（记录每次真实退款）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `refund_record`;
CREATE TABLE `refund_record` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `refund_no`     VARCHAR(32)   NOT NULL COMMENT '退款流水号',
    `pay_no`        VARCHAR(32)   NOT NULL COMMENT '原支付单号（逻辑外键 pay_order.pay_no）',
    `order_no`      VARCHAR(32)   NOT NULL COMMENT '订单号（冗余）',
    `refund_amount` DECIMAL(12,2) NOT NULL COMMENT '退款金额',
    `refund_type`   TINYINT       NOT NULL DEFAULT 0 COMMENT '退款类型：0=全额，1=部分',
    `trade_refund_no` VARCHAR(64) DEFAULT NULL COMMENT '第三方退款流水号',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0=处理中，1=成功，2=失败',
    `reason`        VARCHAR(512)  DEFAULT NULL COMMENT '退款原因',
    `refund_time`   DATETIME      DEFAULT NULL COMMENT '退款成功时间',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`, `deleted`),
    KEY `idx_pay_no` (`pay_no`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '退款流水表';