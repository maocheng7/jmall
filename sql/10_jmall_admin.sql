-- ============================================================
-- jmall_admin 平台管理服务库
-- 表：admin_user 管理员 / admin_role 角色 / admin_permission 权限 /
--     admin_user_role 用户角色 / admin_role_permission 角色权限 /
--     home_banner 首页轮播 / sys_config 系统配置 /
--     admin_operation_log 操作日志 / daily_report 每日统计（数据看板）
-- ============================================================

USE `jmall_admin`;

-- ------------------------------------------------------------
-- 1. 管理员表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `username`      VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    `password`      VARCHAR(128) NOT NULL COMMENT '密码（BCrypt加密）',
    `name`          VARCHAR(64)  DEFAULT NULL COMMENT '姓名',
    `avatar`        VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `phone`         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`         VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    `last_login_time` DATETIME   DEFAULT NULL COMMENT '最后登录时间',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`, `deleted`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员表';

-- ------------------------------------------------------------
-- 2. 角色表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `admin_role`;
CREATE TABLE `admin_role` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `role_name`     VARCHAR(64)  NOT NULL COMMENT '角色名称',
    `role_code`     VARCHAR(64)  NOT NULL COMMENT '角色编码（唯一）',
    `description`   VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`, `deleted`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表';

-- ------------------------------------------------------------
-- 3. 权限表（菜单/按钮）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `admin_permission`;
CREATE TABLE `admin_permission` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `parent_id`     BIGINT       NOT NULL DEFAULT 0 COMMENT '父权限ID（0=顶级菜单）',
    `perm_name`     VARCHAR(64)  NOT NULL COMMENT '权限名称',
    `perm_type`     TINYINT      NOT NULL DEFAULT 1 COMMENT '类型：1=菜单，2=按钮',
    `perm_code`     VARCHAR(64)  DEFAULT NULL COMMENT '权限编码（如 admin:user:add）',
    `path`          VARCHAR(128) DEFAULT NULL COMMENT '前端路由路径',
    `icon`          VARCHAR(64)  DEFAULT NULL COMMENT '图标',
    `sort`          INT          NOT NULL DEFAULT 0 COMMENT '排序号',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=停用，1=启用',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_perm_code` (`perm_code`, `deleted`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表';

-- ------------------------------------------------------------
-- 4. 管理员-角色关联表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `admin_user_role`;
CREATE TABLE `admin_user_role` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `user_id`       BIGINT       NOT NULL COMMENT '管理员ID（物理外键 admin_user.id）',
    `role_id`       BIGINT       NOT NULL COMMENT '角色ID（物理外键 admin_role.id）',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`),
    CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `admin_user` (`id`),
    CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `admin_role` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员-角色关联表';

-- ------------------------------------------------------------
-- 5. 角色-权限关联表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `admin_role_permission`;
CREATE TABLE `admin_role_permission` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `role_id`       BIGINT       NOT NULL COMMENT '角色ID（物理外键 admin_role.id）',
    `permission_id` BIGINT       NOT NULL COMMENT '权限ID（物理外键 admin_permission.id）',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_perm` (`role_id`, `permission_id`),
    KEY `idx_permission_id` (`permission_id`),
    CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `admin_role` (`id`),
    CONSTRAINT `fk_rp_perm` FOREIGN KEY (`permission_id`) REFERENCES `admin_permission` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色-权限关联表';

-- ------------------------------------------------------------
-- 6. 首页轮播图/推荐位表（首页内容管理）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `home_banner`;
CREATE TABLE `home_banner` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `title`         VARCHAR(64)  DEFAULT NULL COMMENT '标题',
    `image_url`     VARCHAR(255) NOT NULL COMMENT '图片URL',
    `link_url`      VARCHAR(255) DEFAULT NULL COMMENT '跳转链接（商品/活动/分类）',
    `position`      TINYINT      NOT NULL DEFAULT 1 COMMENT '位置：1=顶部轮播，2=推荐位',
    `sort`          INT          NOT NULL DEFAULT 0 COMMENT '排序号',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=下架，1=上架',
    `start_time`    DATETIME     DEFAULT NULL COMMENT '展示开始时间（NULL=长期）',
    `end_time`      DATETIME     DEFAULT NULL COMMENT '展示结束时间（NULL=长期）',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_position` (`position`, `status`, `sort`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '首页轮播/推荐位表';

-- ------------------------------------------------------------
-- 7. 系统配置表（KV）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `id`            BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `config_key`    VARCHAR(128) NOT NULL COMMENT '配置键',
    `config_value`  TEXT         DEFAULT NULL COMMENT '配置值',
    `config_type`   VARCHAR(32)  DEFAULT NULL COMMENT '配置分组',
    `description`   VARCHAR(255) DEFAULT NULL COMMENT '配置说明',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`, `deleted`),
    KEY `idx_config_type` (`config_type`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表';

-- ------------------------------------------------------------
-- 8. 管理员操作日志表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `admin_operation_log`;
CREATE TABLE `admin_operation_log` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `admin_id`      BIGINT        DEFAULT NULL COMMENT '操作人ID',
    `admin_name`    VARCHAR(64)   DEFAULT NULL COMMENT '操作人名称',
    `module`        VARCHAR(64)   DEFAULT NULL COMMENT '操作模块',
    `operation`     VARCHAR(128)  DEFAULT NULL COMMENT '操作描述',
    `method`        VARCHAR(128)  DEFAULT NULL COMMENT '请求方法',
    `request_url`   VARCHAR(255)  DEFAULT NULL COMMENT '请求URL',
    `request_params` TEXT         DEFAULT NULL COMMENT '请求参数（JSON）',
    `ip`            VARCHAR(64)   DEFAULT NULL COMMENT '操作IP',
    `duration`      BIGINT        DEFAULT NULL COMMENT '耗时（毫秒）',
    `is_success`    TINYINT       NOT NULL DEFAULT 1 COMMENT '是否成功：0=失败，1=成功',
    `error_msg`     VARCHAR(1024) DEFAULT NULL COMMENT '失败信息',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_admin_id` (`admin_id`),
    KEY `idx_module` (`module`, `create_time`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员操作日志表';

-- ------------------------------------------------------------
-- 9. 每日统计报表表（数据看板数据源）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `daily_report`;
CREATE TABLE `daily_report` (
    `id`            BIGINT        NOT NULL COMMENT '主键ID（雪花算法）',
    `report_date`   DATE          NOT NULL COMMENT '统计日期',
    `gmv`           DECIMAL(16,2) NOT NULL DEFAULT 0.00 COMMENT '当日GMV（实付金额）',
    `order_count`   INT           NOT NULL DEFAULT 0 COMMENT '当日订单数',
    `paid_order_count` INT        NOT NULL DEFAULT 0 COMMENT '当日支付订单数',
    `refund_amount` DECIMAL(16,2) NOT NULL DEFAULT 0.00 COMMENT '当日退款金额',
    `new_user_count` INT          NOT NULL DEFAULT 0 COMMENT '新增注册用户数',
    `new_merchant_count` INT      NOT NULL DEFAULT 0 COMMENT '新增商家数',
    `active_user_count` INT       NOT NULL DEFAULT 0 COMMENT '活跃用户数',
    `product_count`  INT          NOT NULL DEFAULT 0 COMMENT '在售商品数',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_report_date` (`report_date`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '每日统计报表表';