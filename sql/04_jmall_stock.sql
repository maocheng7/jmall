-- ============================================================
-- jmall_stock 库存服务库
-- 表：stock 库存表 / stock_record 库存流水表
-- 说明：库存扣减走 Redis 缓存 + MQ 异步落库（最终一致性），
--       数据库表为最终数据源，version 字段支持乐观锁。
-- ============================================================

USE `jmall_stock`;

-- ------------------------------------------------------------
-- 1. 库存表（一 SKU 一行）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `stock`;
CREATE TABLE `stock` (
    `id`              BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `sku_id`          BIGINT       NOT NULL COMMENT 'SKU ID（逻辑外键 jmall_product.product_sku.id）',
    `merchant_id`     BIGINT       DEFAULT NULL COMMENT '商家ID（逻辑外键 jmall_merchant.merchant.id）',
    `quantity`        INT          NOT NULL DEFAULT 0 COMMENT '总库存数量',
    `locked_quantity` INT          NOT NULL DEFAULT 0 COMMENT '锁定（预占）库存数量',
    `available_quantity` INT      NOT NULL DEFAULT 0 COMMENT '可用库存 = quantity - locked_quantity',
    `warn_stock`      INT          NOT NULL DEFAULT 10 COMMENT '库存预警阈值',
    `version`         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=停用，1=启用',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sku_id` (`sku_id`, `deleted`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_warn` (`status`, `available_quantity`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '库存表';

-- ------------------------------------------------------------
-- 2. 库存流水表（扣减/回滚明细，MQ 消费后落库）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `stock_record`;
CREATE TABLE `stock_record` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `order_no`      VARCHAR(32)  NOT NULL COMMENT '订单号（逻辑外键 order.order_no）',
    `sku_id`        BIGINT       NOT NULL COMMENT 'SKU ID',
    `quantity`      INT          NOT NULL COMMENT '变动数量（扣减为负，回滚为正）',
    `type`          TINYINT      NOT NULL COMMENT '类型：1=预占锁定，2=确认扣减，3=回滚释放',
    `before_quantity` INT        NOT NULL DEFAULT 0 COMMENT '变动前可用库存',
    `after_quantity`  INT        NOT NULL DEFAULT 0 COMMENT '变动后可用库存',
    `biz_id`        VARCHAR(64)  DEFAULT NULL COMMENT '幂等业务ID（防重复消费）',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_biz_id` (`biz_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_sku_id` (`sku_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '库存流水表';