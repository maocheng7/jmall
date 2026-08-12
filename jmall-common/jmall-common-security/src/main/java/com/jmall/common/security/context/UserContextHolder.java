package com.jmall.common.security.context;

/**
 * 用户上下文持有者
 * <p>
 * 基于 ThreadLocal 存储当前请求的用户上下文，在请求结束时清理。
 * 下游服务通过拦截器从请求头解析用户信息后存入 ThreadLocal。
 * </p>
 *
 * @author jmall
 */
public final class UserContextHolder {

    private UserContextHolder() {
    }

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    /**
     * 设置用户上下文
     */
    public static void set(UserContext context) {
        CONTEXT.set(context);
    }

    /**
     * 获取用户上下文
     */
    public static UserContext get() {
        return CONTEXT.get();
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        UserContext context = CONTEXT.get();
        return context != null ? context.getUserId() : null;
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        UserContext context = CONTEXT.get();
        return context != null ? context.getUsername() : null;
    }

    /**
     * 获取角色
     */
    public static String getRole() {
        UserContext context = CONTEXT.get();
        return context != null ? context.getRole() : null;
    }

    /**
     * 获取商家ID
     */
    public static Long getMerchantId() {
        UserContext context = CONTEXT.get();
        return context != null ? context.getMerchantId() : null;
    }

    /**
     * 判断是否已登录
     */
    public static boolean isLogin() {
        return CONTEXT.get() != null && CONTEXT.get().getUserId() != null;
    }

    /**
     * 判断是否为管理员
     */
    public static boolean isAdmin() {
        return "admin".equals(getRole());
    }

    /**
     * 判断是否为商家
     */
    public static boolean isMerchant() {
        return "merchant".equals(getRole());
    }

    /**
     * 清理上下文（防止内存泄漏）
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
