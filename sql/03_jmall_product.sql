-- ============================================================
-- jmall_product 商品服务库
-- 表：category 分类 / brand 品牌 / product_spu 商品 /
--     product_sku SKU / spec 规格名 / spec_value 规格值 /
--     product_review 评价 / product_question 商品问答
-- ============================================================

USE `jmall_product`;

-- ------------------------------------------------------------
-- 1. 商品分类表（树形结构：parent_id 自引用）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `parent_id`     BIGINT       NOT NULL DEFAULT 0 COMMENT '父分类ID（0=顶级分类）',
    `name`          VARCHAR(64)  NOT NULL COMMENT '分类名称',
    `level`         TINYINT      NOT NULL DEFAULT 1 COMMENT '层级：1=一级，2=二级，3=三级',
    `sort`          INT          NOT NULL DEFAULT 0 COMMENT '排序号（越小越靠前）',
    `icon`          VARCHAR(255) DEFAULT NULL COMMENT '分类图标URL',
    `image`         VARCHAR(255) DEFAULT NULL COMMENT '分类图片URL',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=停用，1=启用',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`, `sort`),
    KEY `idx_level` (`level`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表';

-- ------------------------------------------------------------
-- 2. 品牌表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `brand`;
CREATE TABLE `brand` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `name`          VARCHAR(64)  NOT NULL COMMENT '品牌名称',
    `logo`          VARCHAR(255) DEFAULT NULL COMMENT '品牌Logo URL',
    `description`   VARCHAR(512) DEFAULT NULL COMMENT '品牌描述',
    `letter`        CHAR(1)      DEFAULT NULL COMMENT '品牌首字母（用于检索）',
    `sort`          INT          NOT NULL DEFAULT 0 COMMENT '排序号',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=停用，1=启用',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`, `deleted`),
    KEY `idx_letter` (`letter`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '品牌表';

-- ------------------------------------------------------------
-- 3. 商品 SPU 表（商品主体）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `product_spu`;
CREATE TABLE `product_spu` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `spu_no`        VARCHAR(32)   NOT NULL COMMENT '商品编号',
    `merchant_id`   BIGINT        NOT NULL COMMENT '商家ID（逻辑外键 jmall_merchant.merchant.id）',
    `shop_id`       BIGINT        DEFAULT NULL COMMENT '店铺ID（逻辑外键 jmall_merchant.shop.id）',
    `category_id`   BIGINT        NOT NULL COMMENT '三级分类ID（逻辑外键 category.id）',
    `brand_id`      BIGINT        DEFAULT NULL COMMENT '品牌ID（逻辑外键 brand.id）',
    `name`          VARCHAR(255)  NOT NULL COMMENT '商品名称',
    `subtitle`      VARCHAR(512)  DEFAULT NULL COMMENT '副标题/卖点',
    `main_image`    VARCHAR(255)  DEFAULT NULL COMMENT '主图URL',
    `images`        TEXT          DEFAULT NULL COMMENT '详情轮播图URL，JSON数组',
    `detail`        MEDIUMTEXT    DEFAULT NULL COMMENT '商品详情（富文本）',
    `search_keyword` VARCHAR(512) DEFAULT NULL COMMENT '搜索关键词',
    `sales`         INT           NOT NULL DEFAULT 0 COMMENT '销量',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '上下架状态：0=下架，1=上架',
    `audit_status`  TINYINT       NOT NULL DEFAULT 1 COMMENT '审核状态：0=待审核，1=审核通过，2=审核驳回',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_spu_no` (`spu_no`, `deleted`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_brand_id` (`brand_id`),
    KEY `idx_status` (`status`, `audit_status`),
    KEY `idx_name` (`name`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品SPU表';

-- ------------------------------------------------------------
-- 4. 商品 SKU 表（规格售卖单元）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `spu_id`        BIGINT        NOT NULL COMMENT '所属SPU ID（物理外键 product_spu.id）',
    `sku_no`        VARCHAR(32)   NOT NULL COMMENT 'SKU编码',
    `name`          VARCHAR(255)  DEFAULT NULL COMMENT '规格名称（如：红色 128GB 标准版）',
    `spec_value`    VARCHAR(512)  DEFAULT NULL COMMENT '规格值描述（如 "颜色:红色;内存:128GB"）',
    `image`         VARCHAR(255)  DEFAULT NULL COMMENT '规格图片URL',
    `price`         DECIMAL(12,2) NOT NULL COMMENT '销售价格（元）',
    `original_price` DECIMAL(12,2) DEFAULT NULL COMMENT '划线原价（元）',
    `cost_price`    DECIMAL(12,2) DEFAULT NULL COMMENT '成本价（元，商家后台可见）',
    `weight`        DECIMAL(8,2)  NOT NULL DEFAULT 0.00 COMMENT '重量（kg，计算运费用）',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0=禁用，1=启用',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sku_no` (`sku_no`, `deleted`),
    KEY `idx_spu_id` (`spu_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_sku_spu` FOREIGN KEY (`spu_id`) REFERENCES `product_spu` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品SKU表';

-- ------------------------------------------------------------
-- 5. 规格名表（如：颜色、内存、版本）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `spec`;
CREATE TABLE `spec` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `name`          VARCHAR(64)  NOT NULL COMMENT '规格名称',
    `sort`          INT          NOT NULL DEFAULT 0 COMMENT '排序号',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`, `deleted`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '规格名表';

-- ------------------------------------------------------------
-- 6. 规格值表（如：红色、128GB）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `spec_value`;
CREATE TABLE `spec_value` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `spec_id`       BIGINT       NOT NULL COMMENT '所属规格名ID（物理外键 spec.id）',
    `value`         VARCHAR(64)  NOT NULL COMMENT '规格值',
    `sort`          INT          NOT NULL DEFAULT 0 COMMENT '排序号',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_spec_id` (`spec_id`),
    CONSTRAINT `fk_spec_value_spec` FOREIGN KEY (`spec_id`) REFERENCES `spec` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '规格值表';

-- ------------------------------------------------------------
-- 7. 商品评价表（用户购买后评价）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `product_review`;
CREATE TABLE `product_review` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `order_no`      VARCHAR(32)  NOT NULL COMMENT '订单号（逻辑外键 jmall_order.order.order_no）',
    `order_item_id` BIGINT       DEFAULT NULL COMMENT '订单明细ID（逻辑外键 jmall_order.order_item.id）',
    `user_id`       BIGINT       NOT NULL COMMENT '用户ID（逻辑外键 jmall_user.user.id）',
    `spu_id`        BIGINT       NOT NULL COMMENT '商品SPU ID（物理外键 product_spu.id）',
    `sku_id`        BIGINT       DEFAULT NULL COMMENT 'SKU ID（物理外键 product_sku.id）',
    `score`         TINYINT      NOT NULL DEFAULT 5 COMMENT '评分：1-5星',
    `content`       VARCHAR(1024) DEFAULT NULL COMMENT '评价内容',
    `images`        TEXT         DEFAULT NULL COMMENT '评价图片，JSON数组',
    `reply`         VARCHAR(1024) DEFAULT NULL COMMENT '商家回复',
    `reply_time`    DATETIME     DEFAULT NULL COMMENT '商家回复时间',
    `is_anonymous`  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否匿名：0=否，1=是',
    `is_top`        TINYINT      NOT NULL DEFAULT 0 COMMENT '是否置顶：0=否，1=是',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=隐藏，1=显示',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_spu_id` (`spu_id`, `status`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_review_spu` FOREIGN KEY (`spu_id`) REFERENCES `product_spu` (`id`),
    CONSTRAINT `fk_review_sku` FOREIGN KEY (`sku_id`) REFERENCES `product_sku` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品评价表';

-- ------------------------------------------------------------
-- 8. 商品问答表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `product_question`;
CREATE TABLE `product_question` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `spu_id`        BIGINT        NOT NULL COMMENT '商品SPU ID（物理外键 product_spu.id）',
    `user_id`       BIGINT        NOT NULL COMMENT '提问用户ID（逻辑外键 user.id）',
    `question`      VARCHAR(1024) NOT NULL COMMENT '问题内容',
    `answer`        VARCHAR(2048) DEFAULT NULL COMMENT '回答内容',
    `answer_user_id` BIGINT       DEFAULT NULL COMMENT '回答人ID（商家/客服）',
    `answer_time`   DATETIME      DEFAULT NULL COMMENT '回答时间',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0=待回答，1=已回答，2=已屏蔽',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提问时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_spu_id` (`spu_id`, `status`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_question_spu` FOREIGN KEY (`spu_id`) REFERENCES `product_spu` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品问答表';