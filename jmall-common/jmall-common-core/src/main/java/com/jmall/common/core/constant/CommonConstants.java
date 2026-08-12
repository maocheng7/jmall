package com.jmall.common.core.constant;

/**
 * 系统通用常量
 *
 * @author jmall
 */
public final class CommonConstants {

    private CommonConstants() {
    }

    /**
     * 系统名称
     */
    public static final String SYSTEM_NAME = "J-Mall";

    /**
     * 系统版本
     */
    public static final String SYSTEM_VERSION = "1.0.0";

    /**
     * 默认页码
     */
    public static final long DEFAULT_PAGE_NUM = 1L;

    /**
     * 默认每页大小
     */
    public static final long DEFAULT_PAGE_SIZE = 10L;

    /**
     * UTF-8 编码
     */
    public static final String UTF_8 = "UTF-8";

    /**
     * 应用JSON类型
     */
    public static final String APPLICATION_JSON = "application/json;charset=UTF-8";

    /**
     * Token 请求头名称
     */
    public static final String HEADER_TOKEN = "Authorization";

    /**
     * Token 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 用户ID请求头（网关解析后传递给下游服务）
     */
    public static final String HEADER_USER_ID = "X-User-Id";

    /**
     * 用户名请求头
     */
    public static final String HEADER_USERNAME = "X-Username";

    /**
     * 用户角色请求头
     */
    public static final String HEADER_USER_ROLE = "X-User-Role";

    /**
     * 商家ID请求头
     */
    public static final String HEADER_MERCHANT_ID = "X-Merchant-Id";

    /**
     * 请求来源（内部调用标识）
     */
    public static final String HEADER_SOURCE = "X-Request-Source";

    /**
     * 内部调用标识值
     */
    public static final String SOURCE_INTERNAL = "jmall-internal";

    /**
     * 逻辑删除 - 未删除
     */
    public static final int NOT_DELETED = 0;

    /**
     * 逻辑删除 - 已删除
     */
    public static final int DELETED = 1;

    /**
     * 启用状态
     */
    public static final int ENABLED = 1;

    /**
     * 禁用状态
     */
    public static final int DISABLED = 0;
}
