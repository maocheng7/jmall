-- ============================================================
-- jmall_merchant 商家服务库
-- 表：merchant 商家 / merchant_apply 入驻申请 / shop 店铺 /
--     chat_session 客服会话 / chat_message 客服消息
-- ============================================================

USE `jmall_merchant`;

-- ------------------------------------------------------------
-- 1. 商家表（与 user 关联：一个用户可申请成为商家）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `user_id`       BIGINT       NOT NULL COMMENT '关联用户ID（逻辑外键 jmall_user.user.id）',
    `name`          VARCHAR(64)  NOT NULL COMMENT '商家名称',
    `phone`         VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    `logo`          VARCHAR(255) DEFAULT NULL COMMENT '商家Logo',
    `status`        TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0=待审核，1=已入驻，2=已冻结，3=已拒绝',
    `approve_time`  DATETIME     DEFAULT NULL COMMENT '审核通过时间',
    `approve_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入驻时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`, `deleted`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商家表';

-- ------------------------------------------------------------
-- 2. 入驻申请表（一次申请一条记录，支持多次申请）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `merchant_apply`;
CREATE TABLE `merchant_apply` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `user_id`       BIGINT        NOT NULL COMMENT '申请用户ID（逻辑外键 user.id）',
    `merchant_id`   BIGINT        DEFAULT NULL COMMENT '商家ID（审核通过后回填，逻辑外键 merchant.id）',
    `company_name`  VARCHAR(128)  NOT NULL COMMENT '公司名称',
    `company_code`  VARCHAR(64)   NOT NULL COMMENT '统一社会信用代码',
    `contact_name`  VARCHAR(64)   NOT NULL COMMENT '联系人姓名',
    `contact_phone` VARCHAR(20)   NOT NULL COMMENT '联系人手机号',
    `address`       VARCHAR(255)  DEFAULT NULL COMMENT '经营地址',
    `license_image` VARCHAR(255)  DEFAULT NULL COMMENT '营业执照图片URL',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0=待审核，1=通过，2=驳回',
    `audit_remark`  VARCHAR(255)  DEFAULT NULL COMMENT '审核意见',
    `audit_time`    DATETIME      DEFAULT NULL COMMENT '审核时间',
    `apply_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`, `status`),
    KEY `idx_status` (`status`, `apply_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '入驻申请表';

-- ------------------------------------------------------------
-- 3. 店铺表（一商家一店）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `merchant_id`   BIGINT        NOT NULL COMMENT '商家ID（物理外键 merchant.id）',
    `shop_name`     VARCHAR(128)  NOT NULL COMMENT '店铺名称',
    `logo`          VARCHAR(255)  DEFAULT NULL COMMENT '店铺Logo',
    `banner`        VARCHAR(255)  DEFAULT NULL COMMENT '店铺Banner图',
    `description`   VARCHAR(512)  DEFAULT NULL COMMENT '店铺描述',
    `notice`        VARCHAR(512)  DEFAULT NULL COMMENT '店铺公告',
    `phone`         VARCHAR(20)   DEFAULT NULL COMMENT '客服电话',
    `province`      VARCHAR(32)   DEFAULT NULL COMMENT '所在省份',
    `city`          VARCHAR(32)   DEFAULT NULL COMMENT '所在城市',
    `address`       VARCHAR(255)  DEFAULT NULL COMMENT '详细地址',
    `score`         DECIMAL(3,2)  NOT NULL DEFAULT 5.00 COMMENT '店铺评分（0-5）',
    `status`        TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：0=关闭，1=正常',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_merchant_id` (`merchant_id`, `deleted`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_shop_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '店铺表';

-- ------------------------------------------------------------
-- 4. 客服会话表（用户与店铺客服）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `chat_session`;
CREATE TABLE `chat_session` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `session_no`    VARCHAR(32)  NOT NULL COMMENT '会话编号',
    `user_id`       BIGINT       NOT NULL COMMENT '用户ID（逻辑外键 user.id）',
    `merchant_id`   BIGINT       NOT NULL COMMENT '商家ID（逻辑外键 merchant.id）',
    `shop_id`       BIGINT       DEFAULT NULL COMMENT '店铺ID（冗余）',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1=进行中，2=已关闭',
    `last_message`  VARCHAR(1024) DEFAULT NULL COMMENT '最后一条消息',
    `last_time`     DATETIME     DEFAULT NULL COMMENT '最后消息时间',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_no` (`session_no`, `deleted`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '客服会话表';

-- ------------------------------------------------------------
-- 5. 客服消息表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `session_id`    BIGINT        NOT NULL COMMENT '会话ID（物理外键 chat_session.id）',
    `sender_type`   TINYINT       NOT NULL COMMENT '发送方：1=用户，2=商家客服，3=系统',
    `sender_id`     BIGINT        NOT NULL COMMENT '发送人ID',
    `content_type`  TINYINT       NOT NULL DEFAULT 0 COMMENT '内容类型：0=文本，1=图片，2=商品卡片，3=订单卡片',
    `content`       VARCHAR(2048) NOT NULL COMMENT '消息内容',
    `is_read`       TINYINT       NOT NULL DEFAULT 0 COMMENT '是否已读：0=未读，1=已读',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (`id`),
    KEY `idx_session_id` (`session_id`, `create_time`),
    KEY `idx_sender` (`sender_type`, `sender_id`, `is_read`),
    CONSTRAINT `fk_msg_session` FOREIGN KEY (`session_id`) REFERENCES `chat_session` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '客服消息表';