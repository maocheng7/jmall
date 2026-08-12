package com.jmall.common.core.constant;

/**
 * Redis Key 常量定义
 * <p>
 * 统一管理所有 Redis Key，避免散落各处。命名规范：{@code jmall:模块:业务:标识}
 * </p>
 *
 * @author jmall
 */
public final class RedisConstants {

    private RedisConstants() {
    }

    /**
     * Key 前缀
     */
    public static final String KEY_PREFIX = "jmall:";

    /* ==================== 认证模块 ==================== */
    /** 登录验证码：jmall:auth:code:{phone} */
    public static final String AUTH_CODE_KEY = KEY_PREFIX + "auth:code:";
    /** 登录Token：jmall:auth:token:{userId} */
    public static final String AUTH_TOKEN_KEY = KEY_PREFIX + "auth:token:";
    /** 登录错误次数：jmall:auth:error:{phone} */
    public static final String AUTH_ERROR_COUNT_KEY = KEY_PREFIX + "auth:error:";
    /** 登录验证码有效期（秒） */
    public static final long AUTH_CODE_TTL = 300L;
    /** 登录错误次数有效期（秒） */
    public static final long AUTH_ERROR_TTL = 1800L;
    /** 最大登录错误次数 */
    public static final int MAX_LOGIN_ERROR = 5;

    /* ==================== 商品模块 ==================== */
    /** 商品详情缓存：jmall:product:detail:{skuId} */
    public static final String PRODUCT_DETAIL_KEY = KEY_PREFIX + "product:detail:";
    /** 商品分类树缓存：jmall:product:category:tree */
    public static final String PRODUCT_CATEGORY_TREE_KEY = KEY_PREFIX + "product:category:tree";
    /** 商品详情缓存有效期（秒） */
    public static final long PRODUCT_DETAIL_TTL = 3600L;

    /* ==================== 购物车模块 ==================== */
    /** 购物车：jmall:cart:{userId} */
    public static final String CART_KEY = KEY_PREFIX + "cart:";

    /* ==================== 库存模块 ==================== */
    /** 库存缓存：jmall:stock:{skuId} */
    public static final String STOCK_KEY = KEY_PREFIX + "stock:";
    /** 库存锁定：jmall:stock:lock:{skuId} */
    public static final String STOCK_LOCK_KEY = KEY_PREFIX + "stock:lock:";

    /* ==================== 秒杀模块 ==================== */
    /** 秒杀库存：jmall:seckill:stock:{activityId} */
    public static final String SECKILL_STOCK_KEY = KEY_PREFIX + "seckill:stock:";
    /** 秒杀活动详情：jmall:seckill:activity:{activityId} */
    public static final String SECKILL_ACTIVITY_KEY = KEY_PREFIX + "seckill:activity:";
    /** 秒杀已购用户集合：jmall:seckill:bought:{activityId} */
    public static final String SECKILL_BOUGHT_KEY = KEY_PREFIX + "seckill:bought:";
    /** 秒杀排队队列：jmall:seckill:queue:{activityId} */
    public static final String SECKILL_QUEUE_KEY = KEY_PREFIX + "seckill:queue:";

    /* ==================== 优惠券模块 ==================== */
    /** 优惠券库存：jmall:coupon:stock:{couponId} */
    public static final String COUPON_STOCK_KEY = KEY_PREFIX + "coupon:stock:";
    /** 用户已领优惠券：jmall:coupon:user:{userId} */
    public static final String COUPON_USER_KEY = KEY_PREFIX + "coupon:user:";

    /* ==================== 订单模块 ==================== */
    /** 订单分布式锁：jmall:order:lock:{orderNo} */
    public static final String ORDER_LOCK_KEY = KEY_PREFIX + "order:lock:";
    /** 订单号生成序列：jmall:order:seq:{yyyyMMdd} */
    public static final String ORDER_SEQ_KEY = KEY_PREFIX + "order:seq:";

    /* ==================== 分布式锁 ==================== */
    /** 通用分布式锁前缀：jmall:lock:{lockName} */
    public static final String LOCK_PREFIX = KEY_PREFIX + "lock:";
}
