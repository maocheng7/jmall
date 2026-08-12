-- ============================================================
-- jmall_auth 认证服务库
-- 表：auth_account  认证账号表
-- 说明：认证账号与用户基础资料分离（用户资料在 jmall_user.user），
--       auth 服务仅保存登录凭证（密码/微信 openid）。
-- ============================================================

USE `jmall_auth`;

-- ------------------------------------------------------------
-- 1. 认证账号表：手机号/用户名 + 密码 或 微信 openid 登录
--    与 jmall_user.user 通过 user_id 逻辑关联（跨库，不建物理外键）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `auth_account`;
CREATE TABLE `auth_account` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `user_id`       BIGINT       NOT NULL COMMENT '用户ID（关联 jmall_user.user.id，逻辑外键）',
    `phone`         VARCHAR(20)  DEFAULT NULL COMMENT '手机号（唯一登录凭证）',
    `username`      VARCHAR(64)  DEFAULT NULL COMMENT '用户名（可选登录凭证）',
    `password`      VARCHAR(128) DEFAULT NULL COMMENT '密码（BCrypt加密）',
    `wx_openid`     VARCHAR(64)  DEFAULT NULL COMMENT '微信小程序 openid',
    `wx_unionid`    VARCHAR(64)  DEFAULT NULL COMMENT '微信开放平台 unionid',
    `login_type`    TINYINT      NOT NULL DEFAULT 1 COMMENT '账号类型：1=密码登录，2=短信登录，3=微信登录',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    `last_login_ip` VARCHAR(64)  DEFAULT NULL COMMENT '最后登录IP',
    `last_login_time` DATETIME   DEFAULT NULL COMMENT '最后登录时间',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`, `deleted`),
    UNIQUE KEY `uk_username` (`username`, `deleted`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_wx_openid` (`wx_openid`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '认证账号表';