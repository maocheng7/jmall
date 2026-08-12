# J-Mall 数据库设计说明

> 第2步交付物：11 个业务库完整建表 SQL。本文件为设计规范、表清单与表关系总览。

## 一、库划分（微服务分库）

| 库 | 服务 | 表数 | 执行文件 |
|----|------|------|---------|
| `jmall_auth` | jmall-auth 认证 | 1 | 01_jmall_auth.sql |
| `jmall_user` | jmall-user 用户 | 4 | 02_jmall_user.sql |
| `jmall_product` | jmall-product 商品 | 8 | 03_jmall_product.sql |
| `jmall_stock` | jmall-stock 库存 | 2 | 04_jmall_stock.sql |
| `jmall_logistics` | jmall-logistics 物流 | 2 | 05_jmall_logistics.sql |
| `jmall_order` | jmall-order 订单 | 4 | 06_jmall_order.sql |
| `jmall_pay` | jmall-pay 支付 | 2 | 07_jmall_pay.sql |
| `jmall_coupon` | jmall-coupon 优惠 | 5 | 08_jmall_coupon.sql |
| `jmall_merchant` | jmall-merchant 商家 | 5 | 09_jmall_merchant.sql |
| `jmall_admin` | jmall-admin 平台管理 | 9 | 10_jmall_admin.sql |
| `jmall_message` | jmall-message 消息 | 2 | 11_jmall_message.sql |

**无数据库服务**：`jmall-gateway`（网关）、`jmall-cart`（购物车用 Redis Hash）、`jmall-search`（商品索引用 Elasticsearch）。

## 二、通用设计规范

1. **引擎与字符集**：全部 InnoDB + `utf8mb4` / `utf8mb4_0900_ai_ci`（兼容 emoji）。
2. **主键**：`id BIGINT` 雪花 ID（与 MyBatis-Plus `ASSIGN_ID` 策略一致，建表不设自增）。
3. **审计字段**：所有业务表统一含 `create_time`/`update_time`/`deleted`（逻辑删除），与公共模块 `BaseEntity` 对齐。
4. **金额字段**：统一 `DECIMAL(12,2)`（金额）/ `DECIMAL(16,2)`（汇总统计），**禁止使用浮点**。
5. **命名**：表名小写下划线；索引 `idx_字段名`、唯一键 `uk_字段名`、外键 `fk_表_表`。
6. **状态字段**：统一 `TINYINT` + 注释枚举含义，代码侧用枚举映射。

## 三、外键策略（重要）

| 场景 | 策略 | 说明 |
|------|------|------|
| 同库强关联 | **物理外键** | 如 `product_sku.spu_id → product_spu.id`、`order_item.order_id → orders.id`、`logistics_track.delivery_id → delivery.id`、权限关联表等 |
| 跨服务分库 | **逻辑外键 + 索引** | 如 `orders.user_id → user.id`（跨 jmall_order/jmall_user 两个库，物理外键不可行），仅建 `idx_user_id` 索引，关联由 Feign 服务调用保证 |
| 金额/状态快照 | **冗余字段** | 订单明细冗余商品名/规格/价格快照，收货地址快照到订单表，保证历史数据不被商品/地址变更影响 |

## 四、表关系总览

```
jmall_auth    auth_account ──(user_id 逻辑)──▶ jmall_user.user
jmall_user    user ◀── user_address / user_favorite / user_footprint
jmall_product category(parent_id 自引用)
              brand ◀── product_spu ◀── product_sku
              spec ──▶ spec_value
              product_spu ◀── product_review / product_question
jmall_stock   stock(sku_id 逻辑) / stock_record(order_no 逻辑)
jmall_coupon  coupon ──▶ user_coupon
              seckill_activity ──▶ seckill_sku
jmall_merchant merchant ──▶ shop
              merchant_apply（入驻申请）
              chat_session ──▶ chat_message
jmall_order   orders ──▶ order_item / order_status_record / order_refund
jmall_pay     pay_order ──▶ refund_record
jmall_admin   admin_user ──▶ admin_user_role ──▶ admin_role ──▶ admin_role_permission ──▶ admin_permission
jmall_message message_record / site_message

关键链路：orders(user_id, merchant_id, user_coupon_id, delivery_no)
         → order_item(spu_id, sku_id)
         → pay_order(order_no) → refund_record(pay_no)
         → stock_record(order_no)
         → logistics.delivery(order_no) → logistics_track(delivery_id)

## 五、初始化数据

- 建库：执行 `00_database.sql`
- 建表：按文件序号执行 01~11
- 幂等：建表脚本均带 `DROP TABLE IF EXISTS`，可重复执行；建库用 `CREATE DATABASE IF NOT EXISTS`
- 初始数据（admin 账号、角色、分类等）在对应服务开发步骤（第3/8/9步）交付时以 `data` 脚本补充

## 六、与代码层的映射

| 层 | 对应 |
|----|------|
| 实体类 | 各服务 `entity/` 下，继承 `jmall-common-mybatis` 的 `BaseEntity`（id/createTime/updateTime/deleted） |
| 逻辑删除 | `@TableLogic` + 全局配置 `logic-delete-value: 1` |
| 乐观锁 | `stock.version` 字段配合 `@Version` 与 `OptimisticLockerInnerInterceptor` |
| 雪花ID | `java.time.LocalDateTime` 类型直接映射 `DATETIME`；`@TableId(type = IdType.ASSIGN_ID)` 生成主键 |