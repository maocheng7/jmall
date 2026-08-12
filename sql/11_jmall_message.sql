-- ============================================================
-- jmall_message 消息服务库
-- 表：message_record 消息发送记录 / site_message 站内信
-- ============================================================

USE `jmall_message`;

-- ------------------------------------------------------------
-- 1. 消息发送记录表（短信/站内信/推送统一记录）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `message_record`;
CREATE TABLE `message_record` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `type`          VARCHAR(16)   NOT NULL COMMENT '消息类型：sms=短信，site=站内信，push=APP推送',
    `receiver`      VARCHAR(64)   NOT NULL COMMENT '接收者（手机号/用户ID/推送token）',
    `user_id`       BIGINT        DEFAULT NULL COMMENT '用户ID（站内信/推送时填写）',
    `template_code` VARCHAR(64)   DEFAULT NULL COMMENT '模板编码',
    `params`        TEXT          DEFAULT NULL COMMENT '模板参数（JSON）',
    `content`       VARCHAR(2048) DEFAULT NULL COMMENT '最终发送内容',
    `biz_id`        VARCHAR(64)   DEFAULT NULL COMMENT '业务ID（幂等控制，如订单号）',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0=待发送，1=发送成功，2=发送失败',
    `error_msg`     VARCHAR(512)  DEFAULT NULL COMMENT '失败原因',
    `send_time`     DATETIME      DEFAULT NULL COMMENT '发送时间',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_biz_id` (`biz_id`, `type`, `receiver`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息发送记录表';

-- ------------------------------------------------------------
-- 2. 站内信表（用户中心的消息列表）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `site_message`;
CREATE TABLE `site_message` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `user_id`       BIGINT        NOT NULL COMMENT '接收用户ID（逻辑外键 user.id）',
    `title`         VARCHAR(128)  NOT NULL COMMENT '消息标题',
    `content`       VARCHAR(2048) NOT NULL COMMENT '消息内容',
    `msg_type`      TINYINT       NOT NULL DEFAULT 0 COMMENT '类型：0=系统通知，1=订单消息，2=营销消息，3=物流消息',
    `link_url`      VARCHAR(255)  DEFAULT NULL COMMENT '跳转链接',
    `is_read`       TINYINT       NOT NULL DEFAULT 0 COMMENT '是否已读：0=未读，1=已读',
    `read_time`     DATETIME      DEFAULT NULL COMMENT '阅读时间',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_read` (`user_id`, `is_read`, `create_time`),
    KEY `idx_msg_type` (`msg_type`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '站内信表';