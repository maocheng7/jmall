-- ============================================================
-- jmall_logistics 物流服务库
-- 表：delivery 运单表 / logistics_track 物流轨迹表
-- ============================================================

USE `jmall_logistics`;

-- ------------------------------------------------------------
-- 1. 物流运单表（一次发货对应一单）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `delivery`;
CREATE TABLE `delivery` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `delivery_no`   VARCHAR(32)  NOT NULL COMMENT '发货单号',
    `order_no`      VARCHAR(32)  NOT NULL COMMENT '订单号（逻辑外键 jmall_order.order.order_no）',
    `logistics_no`  VARCHAR(64)  NOT NULL COMMENT '快递运单号',
    `logistics_com` VARCHAR(32)  NOT NULL DEFAULT 'SF' COMMENT '快递公司编码：SF=顺丰，KDN=快递鸟，ZTO=中通，YTO=圆通，STO=申通',
    `receiver_name` VARCHAR(64)  NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人手机号',
    `receiver_address` VARCHAR(512) NOT NULL COMMENT '收货地址（省市区+详细地址）',
    `sender_name`   VARCHAR(64)  DEFAULT NULL COMMENT '发货人姓名（店铺）',
    `status`        TINYINT      NOT NULL DEFAULT 0 COMMENT '物流状态：0=待揽收，1=运输中，2=已签收，3=异常',
    `expect_arrive_time` DATETIME DEFAULT NULL COMMENT '预计送达时间',
    `sign_time`     DATETIME     DEFAULT NULL COMMENT '签收时间',
    `remark`        VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_delivery_no` (`delivery_no`, `deleted`),
    UNIQUE KEY `uk_order_no` (`order_no`, `deleted`),
    KEY `idx_logistics_no` (`logistics_no`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '物流运单表';

-- ------------------------------------------------------------
-- 2. 物流轨迹表（一条状态变化一行，按时间倒序展示）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `logistics_track`;
CREATE TABLE `logistics_track` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `delivery_id`   BIGINT       NOT NULL COMMENT '运单ID（物理外键 delivery.id）',
    `logistics_no`  VARCHAR(64)  NOT NULL COMMENT '快递运单号（冗余便于查询）',
    `track_info`    VARCHAR(1024) NOT NULL COMMENT '轨迹描述',
    `track_status`  TINYINT      NOT NULL DEFAULT 1 COMMENT '轨迹状态：0=揽收，1=运输中，2=派件，3=签收，4=异常',
    `operator`      VARCHAR(64)  DEFAULT NULL COMMENT '操作人/网点',
    `track_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '轨迹发生时间',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_delivery_id` (`delivery_id`, `track_time`),
    KEY `idx_logistics_no` (`logistics_no`),
    KEY `idx_track_time` (`track_time`),
    CONSTRAINT `fk_track_delivery` FOREIGN KEY (`delivery_id`) REFERENCES `delivery` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '物流轨迹表';