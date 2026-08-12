-- ============================================================
-- jmall_user 用户服务库
-- 表：user 用户表 / user_address 收货地址 / user_favorite 收藏 / user_footprint 浏览足迹
-- ============================================================

USE `jmall_user`;

-- ------------------------------------------------------------
-- 1. 用户表：用户基本信息（账号凭证在 jmall_auth.auth_account）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `username`      VARCHAR(64)  DEFAULT NULL COMMENT '用户名',
    `nickname`      VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `phone`         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`         VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `avatar`        VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `gender`        TINYINT      NOT NULL DEFAULT 0 COMMENT '性别：0=未知，1=男，2=女',
    `birthday`      DATE         DEFAULT NULL COMMENT '生日',
    `level`         INT          NOT NULL DEFAULT 1 COMMENT '会员等级：1=普通，2=银卡，3=金卡，4=钻石',
    `points`        INT          NOT NULL DEFAULT 0 COMMENT '积分余额',
    `balance`       DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额（元）',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`, `deleted`),
    KEY `idx_username` (`username`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';

-- ------------------------------------------------------------
-- 2. 收货地址表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
    `id`              BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `user_id`         BIGINT       NOT NULL COMMENT '用户ID（逻辑外键 user.id）',
    `receiver_name`   VARCHAR(64)  NOT NULL COMMENT '收货人姓名',
    `receiver_phone`  VARCHAR(20)  NOT NULL COMMENT '收货人手机号',
    `province`        VARCHAR(32)  NOT NULL COMMENT '省份',
    `city`            VARCHAR(32)  NOT NULL COMMENT '城市',
    `district`        VARCHAR(32)  NOT NULL COMMENT '区/县',
    `detail_address`  VARCHAR(255) NOT NULL COMMENT '详细地址',
    `postcode`        VARCHAR(10)  DEFAULT NULL COMMENT '邮编',
    `is_default`      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认：0=否，1=是',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_default` (`user_id`, `is_default`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收货地址表';

-- ------------------------------------------------------------
-- 3. 用户收藏表（按 SPU 收藏）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `user_id`       BIGINT       NOT NULL COMMENT '用户ID（逻辑外键 user.id）',
    `spu_id`        BIGINT       NOT NULL COMMENT '商品SPU ID（逻辑外键 product_spu.id）',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_spu` (`user_id`, `spu_id`, `deleted`),
    KEY `idx_spu_id` (`spu_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户收藏表';

-- ------------------------------------------------------------
-- 4. 用户浏览足迹表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `user_footprint`;
CREATE TABLE `user_footprint` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `user_id`       BIGINT       NOT NULL COMMENT '用户ID（逻辑外键 user.id）',
    `spu_id`        BIGINT       NOT NULL COMMENT '商品SPU ID（逻辑外键 jmall_product.product_spu.id）',
    `sku_id`        BIGINT       DEFAULT NULL COMMENT 'SKU ID（逻辑外键 jmall_product.product_sku.id）',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_time` (`user_id`, `create_time`),
    KEY `idx_spu_id` (`spu_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户浏览足迹表';