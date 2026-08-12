-- ============================================================
-- jmall_coupon 优惠服务库
-- 表：coupon 优惠券模板 / user_coupon 用户优惠券 / promotion 营销活动（满减满折）/
--     seckill_activity 秒杀活动 / seckill_sku 秒杀商品
-- ============================================================

USE `jmall_coupon`;

-- ------------------------------------------------------------
-- 1. 优惠券模板表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `merchant_id`   BIGINT        DEFAULT NULL COMMENT '商家ID（NULL=平台券，有值=商家券）',
    `name`          VARCHAR(64)   NOT NULL COMMENT '优惠券名称',
    `type`          TINYINT       NOT NULL COMMENT '类型：1=满减券，2=折扣券，3=无门槛券',
    `discount_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '优惠金额（满减/无门槛）',
    `discount_rate` DECIMAL(3,2)  DEFAULT NULL COMMENT '折扣率（折扣券，如 0.85）',
    `min_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '使用门槛（满多少可用，0=无门槛）',
    `total_count`   INT           NOT NULL DEFAULT 0 COMMENT '发行总量（0=不限量）',
    `issued_count`  INT           NOT NULL DEFAULT 0 COMMENT '已领取数量',
    `used_count`    INT           NOT NULL DEFAULT 0 COMMENT '已使用数量',
    `per_user_limit` INT          NOT NULL DEFAULT 1 COMMENT '每人限领数量',
    `start_time`    DATETIME      NOT NULL COMMENT '领取开始时间',
    `end_time`      DATETIME      NOT NULL COMMENT '领取结束时间',
    `valid_days`    INT           DEFAULT NULL COMMENT '领取后有效天数（NULL=按固定有效期）',
    `valid_start_time` DATETIME   DEFAULT NULL COMMENT '固定有效期开始',
    `valid_end_time`   DATETIME   DEFAULT NULL COMMENT '固定有效期结束',
    `scope_type`    TINYINT       NOT NULL DEFAULT 0 COMMENT '适用范围：0=全部商品，1=指定SPU，2=指定分类',
    `scope_json`    TEXT          DEFAULT NULL COMMENT '适用范围明细（SPU/分类ID列表，JSON）',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0=草稿，1=进行中，2=已结束，3=已下架',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`, `start_time`, `end_time`),
    KEY `idx_type` (`type`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '优惠券模板表';

-- ------------------------------------------------------------
-- 2. 用户优惠券表（领券/核销）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `user_id`       BIGINT        NOT NULL COMMENT '用户ID（逻辑外键 user.id）',
    `coupon_id`     BIGINT        NOT NULL COMMENT '优惠券模板ID（物理外键 coupon.id）',
    `coupon_name`   VARCHAR(64)   NOT NULL COMMENT '券名称（领取时快照）',
    `type`          TINYINT       NOT NULL COMMENT '券类型（快照）',
    `discount_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '优惠金额（快照）',
    `discount_rate` DECIMAL(3,2)  DEFAULT NULL COMMENT '折扣率（快照）',
    `min_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '使用门槛（快照）',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0=未使用，1=已使用，2=已过期',
    `order_no`      VARCHAR(32)   DEFAULT NULL COMMENT '核销订单号',
    `use_time`      DATETIME      DEFAULT NULL COMMENT '使用时间',
    `expire_time`   DATETIME      NOT NULL COMMENT '过期时间',
    `received_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_coupon_id` (`coupon_id`),
    KEY `idx_expire_time` (`expire_time`),
    CONSTRAINT `fk_user_coupon_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户优惠券表';

-- ------------------------------------------------------------
-- 3. 营销活动表（满减/满折，跨店铺优惠）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `promotion`;
CREATE TABLE `promotion` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `merchant_id`   BIGINT        DEFAULT NULL COMMENT '商家ID（NULL=平台活动）',
    `name`          VARCHAR(64)   NOT NULL COMMENT '活动名称',
    `type`          TINYINT       NOT NULL COMMENT '类型：1=满减，2=满折',
    `threshold_amount` DECIMAL(12,2) NOT NULL COMMENT '满额条件',
    `discount_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '减免金额（满减）',
    `discount_rate` DECIMAL(3,2)  DEFAULT NULL COMMENT '折扣率（满折，如 0.8）',
    `scope_type`    TINYINT       NOT NULL DEFAULT 0 COMMENT '适用范围：0=全场，1=指定SPU，2=指定分类',
    `scope_ids`     TEXT          DEFAULT NULL COMMENT '适用范围明细（JSON）',
    `start_time`    DATETIME      NOT NULL COMMENT '活动开始时间',
    `end_time`      DATETIME      NOT NULL COMMENT '活动结束时间',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0=草稿，1=进行中，2=已结束，3=已下架',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`, `start_time`, `end_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '营销活动表';

-- ------------------------------------------------------------
-- 4. 秒杀活动表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `seckill_activity`;
CREATE TABLE `seckill_activity` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `activity_name` VARCHAR(128) NOT NULL COMMENT '活动名称',
    `start_time`    DATETIME     NOT NULL COMMENT '开始时间',
    `end_time`      DATETIME     NOT NULL COMMENT '结束时间',
    `limit_per_user` INT         NOT NULL DEFAULT 1 COMMENT '每人限购件数',
    `status`        TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0=未开始，1=进行中，2=已结束',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`, `start_time`, `end_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '秒杀活动表';

-- ------------------------------------------------------------
-- 5. 秒杀商品表（活动与 SKU 关联）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `seckill_sku`;
CREATE TABLE `seckill_sku` (
    `id`             BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `activity_id`    BIGINT        NOT NULL COMMENT '秒杀活动ID（物理外键 seckill_activity.id）',
    `spu_id`         BIGINT        NOT NULL COMMENT 'SPU ID（逻辑外键 product_spu.id）',
    `sku_id`         BIGINT        NOT NULL COMMENT 'SKU ID（逻辑外键 product_sku.id）',
    `seckill_price`  DECIMAL(12,2) NOT NULL COMMENT '秒杀价',
    `seckill_stock`  INT           NOT NULL COMMENT '秒杀库存数量',
    `sold_count`     INT           NOT NULL DEFAULT 0 COMMENT '已售数量',
    `limit_per_user` INT           NOT NULL DEFAULT 1 COMMENT '每人限购件数',
    `status`         TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0=参与活动，1=暂停',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_sku_id` (`sku_id`),
    CONSTRAINT `fk_seckill_sku_activity` FOREIGN KEY (`activity_id`) REFERENCES `seckill_activity` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '秒杀商品表';