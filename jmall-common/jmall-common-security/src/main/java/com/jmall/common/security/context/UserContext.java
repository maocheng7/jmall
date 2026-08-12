package com.jmall.common.security.context;

import lombok.Data;

/**
 * 用户上下文信息
 * <p>
 * 封装当前请求的用户身份信息，由网关解析 Token 后通过请求头传递给下游服务，
 * 下游服务通过 {@link UserContextHolder} 获取当前用户。
 * </p>
 *
 * @author jmall
 */
@Data
public class UserContext {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 角色标识（user=普通用户, merchant=商家, admin=管理员）
     */
    private String role;

    /**
     * 商家ID（角色为商家时有值）
     */
    private Long merchantId;

    /**
     * 登录方式（password=密码, sms=短信, wechat=微信）
     */
    private String loginType;

    public UserContext() {
    }

    public UserContext(Long userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
}
