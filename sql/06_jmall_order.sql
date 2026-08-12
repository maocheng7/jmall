-- ============================================================
-- jmall_order 订单服务库
-- 表：order 订单主表 / order_item 订单明细 / order_status_record 状态流转记录 / order_refund 退款单
-- ============================================================

USE `jmall_order`;

-- ------------------------------------------------------------
-- 1. 订单主表（一个订单=一个店铺，同一店铺商品合单）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id`             BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `order_no`       VARCHAR(32)   NOT NULL COMMENT '订单号（业务主键，唯一）',
    `user_id`        BIGINT        NOT NULL COMMENT '用户ID（逻辑外键 jmall_user.user.id）',
    `merchant_id`    BIGINT        NOT NULL COMMENT '商家ID（逻辑外键 jmall_merchant.merchant.id）',
    `shop_id`        BIGINT        DEFAULT NULL COMMENT '店铺ID（逻辑外键 shop.id）',
    `status`         TINYINT       NOT NULL DEFAULT 0 COMMENT '订单状态：0=待付款，1=待发货，2=待收货，3=已完成，4=已取消，5=退款中，6=已退款',
    `total_amount`   DECIMAL(12,2) NOT NULL COMMENT '商品总金额',
    `discount_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额（优惠券+满减）',
    `freight_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '运费',
    `pay_amount`     DECIMAL(12,2) NOT NULL COMMENT '实付金额 = total - discount + freight',
    `pay_type`       TINYINT       DEFAULT NULL COMMENT '支付方式：1=微信，2=支付宝，3=余额，4=模拟',
    `pay_time`       DATETIME      DEFAULT NULL COMMENT '支付时间',
    `user_coupon_id` BIGINT        DEFAULT NULL COMMENT '使用的用户优惠券ID（逻辑外键 jmall_coupon.user_coupon.id）',
    `receiver_name`  VARCHAR(64)   NOT NULL COMMENT '收货人姓名（下单时快照）',
    `receiver_phone` VARCHAR(20)   NOT NULL COMMENT '收货人手机号（快照）',
    `receiver_address` VARCHAR(512) NOT NULL COMMENT '收货地址（快照：省市区+详细地址）',
    `remark`         VARCHAR(512)  DEFAULT NULL COMMENT '买家备注',
    `source`         TINYINT       NOT NULL DEFAULT 1 COMMENT '订单来源：1=普通下单，2=秒杀，3=拼团',
    `is_seckill`     TINYINT       NOT NULL DEFAULT 0 COMMENT '是否秒杀订单：0=否，1=是',
    `cancel_reason`  VARCHAR(255)  DEFAULT NULL COMMENT '取消原因',
    `cancel_time`    DATETIME      DEFAULT NULL COMMENT '取消时间',
    `comment_status` TINYINT       NOT NULL DEFAULT 0 COMMENT '评价状态：0=未评价，1=已评价',
    `delivery_no`    VARCHAR(32)   DEFAULT NULL COMMENT '发货单号（关联 logistics.delivery.delivery_no）',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`, `deleted`),
    KEY `idx_user_id` (`user_id`, `status`),
    KEY `idx_merchant_id` (`merchant_id`, `status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_pay_time` (`pay_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单主表';

-- ------------------------------------------------------------
-- 2. 订单明细表（一个订单多条商品）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `order_id`      BIGINT        NOT NULL COMMENT '订单ID（物理外键 orders.id）',
    `order_no`      VARCHAR(32)   NOT NULL COMMENT '订单号（冗余，避免关联查询）',
    `user_id`       BIGINT        NOT NULL COMMENT '用户ID（冗余）',
    `spu_id`        BIGINT        NOT NULL COMMENT '商品SPU ID（逻辑外键 product_spu.id）',
    `sku_id`        BIGINT        NOT NULL COMMENT 'SKU ID（逻辑外键 product_sku.id）',
    `product_name`  VARCHAR(255)  NOT NULL COMMENT '商品名称（下单快照）',
    `sku_spec`      VARCHAR(512)  DEFAULT NULL COMMENT '规格值描述（快照）',
    `product_image` VARCHAR(255)  DEFAULT NULL COMMENT '商品图片（快照）',
    `price`         DECIMAL(12,2) NOT NULL COMMENT '成交单价（快照）',
    `quantity`      INT           NOT NULL COMMENT '购买数量',
    `total_amount`  DECIMAL(12,2) NOT NULL COMMENT '小计金额 = price * quantity',
    `comment_status` TINYINT      NOT NULL DEFAULT 0 COMMENT '评价状态：0=未评价，1=已评价',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_sku_id` (`sku_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单明细表';

-- ------------------------------------------------------------
-- 3. 订单状态流转记录表（审计用）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `order_status_record`;
CREATE TABLE `order_status_record` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `order_no`      VARCHAR(32)  NOT NULL COMMENT '订单号（逻辑外键 orders.order_no）',
    `from_status`   TINYINT      DEFAULT NULL COMMENT '原状态（NULL=初始）',
    `to_status`     TINYINT      NOT NULL COMMENT '新状态',
    `remark`        VARCHAR(255) DEFAULT NULL COMMENT '变更说明',
    `operator`      VARCHAR(64)  DEFAULT NULL COMMENT '操作人（USER/系统/商家）',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_no` (`order_no`, `create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单状态流转记录表';

-- ------------------------------------------------------------
-- 4. 退款申请表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `order_refund`;
CREATE TABLE `order_refund` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `refund_no`     VARCHAR(32)   NOT NULL COMMENT '退款单号',
    `order_no`      VARCHAR(32)   NOT NULL COMMENT '订单号（逻辑外键 orders.order_no）',
    `order_item_id` BIGINT        DEFAULT NULL COMMENT '订单明细ID（部分退款时指定，物理外键 order_item.id）',
    `user_id`       BIGINT        NOT NULL COMMENT '申请用户ID',
    `merchant_id`   BIGINT        DEFAULT NULL COMMENT '商家ID',
    `refund_amount` DECIMAL(12,2) NOT NULL COMMENT '退款金额',
    `reason`        VARCHAR(512)  DEFAULT NULL COMMENT '退款原因',
    `images`        TEXT          DEFAULT NULL COMMENT '凭证图片，JSON数组',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0=申请中，1=商家同意，2=商家拒绝，3=退款中，4=退款成功，5=关闭',
    `audit_remark`  VARCHAR(255)  DEFAULT NULL COMMENT '审核备注',
    `audit_time`    DATETIME      DEFAULT NULL COMMENT '审核时间',
    `refund_time`   DATETIME      DEFAULT NULL COMMENT '退款完成时间',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`, `deleted`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_refund_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_item` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '退款申请表';