package com.jmall.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送短信验证码请求参数
 *
 * @author jmall
 */
@Data
public class SmsSendDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 验证码用途：register=注册，login=登录，reset=重置密码
     */
    @NotBlank(message = "用途不能为空")
    @Pattern(regexp = "^(register|login|reset)$", message = "用途不正确")
    private String scene;
}