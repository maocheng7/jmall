package com.jmall.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录请求参数
 * <p>
 * 登录方式由 loginType 区分：
 * 1=密码登录（phone + password）
 * 2=短信登录（phone + code）
 * 3=微信登录（wxOpenid）
 * </p>
 *
 * @author jmall
 */
@Data
public class LoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 登录方式：1=密码，2=短信，3=微信
     */
    @NotNull(message = "登录方式不能为空")
    private Integer loginType;

    /**
     * 手机号（密码/短信登录）
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 密码（密码登录）
     */
    private String password;

    /**
     * 短信验证码（短信登录）
     */
    private String code;

    /**
     * 微信 openid（微信登录）
     */
    private String wxOpenid;
}