-- ============================================================
-- J-Mall 数据库初始化脚本 - 建库语句
-- 说明：搜索服务(jmall-search)使用 Elasticsearch、购物车服务(jmall-cart)
--       使用 Redis，无需建库；网关(jmall-gateway)无数据库。
-- 执行顺序：先执行本文件创建所有库，再按序号执行各库建表脚本。
-- 字符集：utf8mb4（兼容 emoji），排序规则 utf8mb4_unicode_ci（MySQL 8.0 默认）
-- ============================================================

CREATE DATABASE IF NOT EXISTS `jmall_auth`      DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `jmall_user`      DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `jmall_product`   DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `jmall_stock`     DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `jmall_order`     DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `jmall_pay`       DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `jmall_coupon`    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `jmall_merchant`  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `jmall_admin`     DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `jmall_message`   DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `jmall_logistics` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;