# J-Mall 仿京东商城系统

> 基于微服务架构的 B2C 电商平台，后端 Java 17 + Spring Boot 3.3 + Spring Cloud Alibaba 2023.x。

## 一、技术栈

| 层次 | 技术 |
|------|------|
| 后端框架 | Java 17 / Spring Boot 3.3.5 / Spring Cloud 2023.0.3 |
| 微服务 | Spring Cloud Alibaba 2023.0.1.2（Nacos 注册+配置）/ Spring Cloud Gateway |
| 鉴权 | Sa-Token 1.39.0 |
| 持久层 | MyBatis-Plus 3.5.7 / MySQL 8.0 / Druid |
| 缓存 | Redis 7 / Redisson（分布式锁） |
| MQ | RocketMQ 5.x（订单/库存异步、秒杀） |
| 搜索 | Elasticsearch 8.x（Java Client） |
| 事务 | Seata（分布式事务，订单链路） |
| 限流 | Sentinel |
| 文档 | SpringDoc OpenAPI 2.6.0 |

## 二、模块结构

```
jmall/
├── pom.xml                         # 父 POM（统一版本管理）
├── jmall-common/                   # 公共模块（按需引入）
│   ├── jmall-common-core           # 常量/枚举/Result/异常/工具（无Spring依赖）
│   ├── jmall-common-web            # 全局异常/参数校验/SpringDoc/CORS
│   ├── jmall-common-redis          # Redis配置/分布式锁/缓存工具
│   ├── jmall-common-mybatis        # MP配置/自动填充/BaseEntity
│   ├── jmall-common-mq             # RocketMQ 生产者/消费者基类
│   ├── jmall-common-es             # ES 客户端配置/BaseEsEntity
│   └── jmall-common-security       # Sa-Token/网关鉴权过滤器/用户上下文/Feign透传
├── jmall-api/                      # 共享API层（Feign 接口 + DTO）
│   ├── jmall-api-user / -product / -stock / -order
│   └── jmall-api-coupon / -merchant / -pay / -message
├── jmall-gateway                   # 网关（8080）
├── jmall-auth                      # 认证服务（8101）
├── jmall-user                      # 用户服务（8102）
├── jmall-product                   # 商品服务（8103）
├── jmall-search                    # 搜索服务（8104）
├── jmall-cart                      # 购物车服务（8105）
├── jmall-order                     # 订单服务（8106）
├── jmall-pay                       # 支付服务（8107）
├── jmall-stock                     # 库存服务（8108）
├── jmall-coupon                    # 优惠服务（8109）
├── jmall-merchant                  # 商家服务（8110）
├── jmall-admin                     # 平台管理服务（8111）
├── jmall-message                   # 消息服务（8112）
├── jmall-logistics                 # 物流服务（8113）
└── config/nacos/                   # Nacos 配置样例（可导入）
```

## 三、环境要求

| 组件 | 版本 | 默认地址 |
|------|------|---------|
| JDK | 17+ | - |
| Maven | 3.8+ | - |
| MySQL | 8.0 | 127.0.0.1:3306 |
| Redis | 7.x | 127.0.0.1:6379 |
| Nacos | 2.3.x | 127.0.0.1:8848 |
| RocketMQ | 5.x | 127.0.0.1:9876 |
| Elasticsearch | 8.x | 127.0.0.1:9200 |
| MinIO | - | 可选 |
| Seata | 2.x | 127.0.0.1:8091 |

## 四、启动步骤

### 1. 环境准备
启动 MySQL、Redis、Nacos、RocketMQ、ES、MinIO（开发阶段可暂不启动 ES/RocketMQ/Seata，对应服务在业务开发完成后再接入）。

### 2. 导入 Nacos 配置
将 `config/nacos/jmall-common.yml` 导入 Nacos 配置中心（`DEFAULT_GROUP`），并创建各服务配置：
- `jmall-auth.yml` / `jmall-user.yml` / `jmall-product.yml` / ...（每个服务一个，含数据源等个性化配置）
- 本仓库各服务的 `application.yml` 已内置默认配置，可直接启动（本地开发也可不用 Nacos 配置中心，仅用注册中心）。

### 3. 编译打包
```bash
mvn clean install -DskipTests
```

### 4. 启动服务（按依赖顺序）
```bash
# 1) 启动 jmall-gateway（网关）
java -jar jmall-gateway/target/jmall-gateway-1.0.0.jar
# 2) 启动 jmall-auth（认证）
java -jar jmall-auth/target/jmall-auth-1.0.0.jar
# 3) 按需启动其余服务
```

或使用 IDE 分别运行各模块的 `JmallXxxApplication` 启动类。

### 5. 访问入口

| 入口 | 地址 |
|------|------|
| 网关入口 | http://127.0.0.1:8080 |
| 各服务 Swagger UI | http://127.0.0.1:{port}/swagger-ui.html |
| Nacos 控制台 | http://127.0.0.1:8848/nacos | 

## 五、每步测试方法

- **第1步（本项目）**：编译通过（`mvn clean install -DskipTests`）；启动 Nacos 后启动 gateway，访问 `http://127.0.0.1:8080/{服务路径}` 验证路由；访问任意服务 Swagger 验证文档生成。
- 后续步骤的测试方法在各步骤交付时说明。

## 七、当前实现进度

### 已完成模块

- 基础工程、公共模块、Nacos/Gateway 骨架
- 数据库设计：11库、44表、SQL校验通过
- 认证服务：注册、密码/短信/微信登录、Sa-Token、验证码、登出
- 用户服务：资料、地址、收藏、足迹
- 商品/搜索：SPU/SKU、分类树、上下架MQ、ES搜索骨架
- 购物车/订单/库存/支付：Redis购物车、下单、库存扣减回滚、模拟支付
- 优惠券基础服务：模板、领券、核销、退款、优惠计算、秒杀基础表服务
- 商家服务：入驻申请、店铺管理、内部查询接口
- 平台管理：管理员/RBAC基础数据、首页轮播、系统配置、日报表
- 消息/物流：消息记录、站内信、短信记录、mock运单和轨迹

### 当前限制与后续增强

1. **Maven 编译**：`mvn clean install -DskipTests` 已通过（Maven 3.9.9 + JDK 17），全部 32 个模块编译成功。
2. **秒杀高并发**：当前 coupon 已有基础业务接口，但还需要把秒杀库存改为 Redis Lua 原子扣减，并通过 RocketMQ 异步创建订单。
3. **分布式事务**：订单当前采用本地事务 + Feign 补偿；生产环境需启用 Seata AT 或可靠消息最终一致方案。
4. **支付渠道**：当前为 mock 支付；微信/支付宝真实签名、回调验签、退款需要配置商户参数后接入。
5. **安全加固**：内部接口应增加网关签名/服务间密钥校验；admin 还需要细粒度 RBAC 注解鉴权。
6. **ES索引**：当前商品上架消费者已打通，但索引文档字段补全和索引初始化脚本需在 ES 环境准备后完善。
7. **前端与部署**：Vue/UniApp、Docker Compose、Nginx、CI/CD 尚未开始，属于后续阶段。

### 推荐下一步顺序

1. 安装 Maven 并完成全工程编译，逐项修复编译错误
2. 增加各服务集成测试和 Testcontainers/本地中间件测试
3. 完善秒杀 Lua + RocketMQ、Seata 事务和支付回调验签
4. 开始前端用户端 Web，再开发商家端/管理端/UniApp
5. 最后补 Docker Compose、Nginx 和部署脚本
