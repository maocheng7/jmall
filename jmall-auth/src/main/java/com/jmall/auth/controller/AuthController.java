package com.jmall.auth.controller;

import com.jmall.auth.dto.LoginDTO;
import com.jmall.auth.dto.RegisterDTO;
import com.jmall.auth.dto.SmsSendDTO;
import com.jmall.auth.service.AuthService;
import com.jmall.auth.vo.AuthLoginVO;
import com.jmall.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * <p>
 * 提供注册、登录、验证码发送、登出接口。
 * </p>
 *
 * @author jmall
 */
@Tag(name = "认证服务", description = "注册/登录/验证码/登出")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 手机号注册
     */
    @Operation(summary = "手机号注册", description = "手机号+密码+短信验证码注册")
    @PostMapping("/register")
    public Result<AuthLoginVO> register(@Valid @RequestBody RegisterDTO dto) {
        return Result.success(authService.register(dto));
    }

    /**
     * 登录（密码/短信/微信）
     */
    @Operation(summary = "用户登录", description = "支持三种方式：loginType 1=密码, 2=短信验证码, 3=微信")
    @PostMapping("/login")
    public Result<AuthLoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    /**
     * 发送短信验证码
     */
    @Operation(summary = "发送短信验证码", description = "场景：register=注册, login=登录, reset=重置密码")
    @PostMapping("/code/send")
    public Result<Void> sendSmsCode(@Valid @RequestBody SmsSendDTO dto) {
        authService.sendSmsCode(dto);
        return Result.success();
    }

    /**
     * 登出
     */
    @Operation(summary = "登出", description = "清除当前用户 Token")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
}
