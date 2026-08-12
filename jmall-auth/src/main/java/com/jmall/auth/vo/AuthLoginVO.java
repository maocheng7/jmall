package com.jmall.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录成功返回视图对象
 *
 * @author jmall
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Token（后续请求放入 Authorization: Bearer <token>）
     */
    private String token;

    /**
     * Token 名称
     */
    private String tokenName;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色（user=普通用户）
     */
    private String role;
}