package com.jmall.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 注册请求参数
 * <p>
 * 支持三种注册方式：
 * 1. 密码注册：phone + password + code（短信验证码）
 * 2. 微信注册：wxOpenid（未绑定用户时自动注册）
 * </p>
 *
 * @author jmall
 */
@Data
public class RegisterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度需在6-20位之间")
    private String password;

    /**
     * 短信验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

    /**
     * 用户名（可选）
     */
    @Size(max = 32, message = "用户名长度不能超过32位")
    private String username;

    /**
     * 昵称（可选）
     */
    @Size(max = 64, message = "昵称长度不能超过64位")
    private String nickname;

    /**
     * 微信 openid（微信一键注册时使用）
     */
    private String wxOpenid;
}