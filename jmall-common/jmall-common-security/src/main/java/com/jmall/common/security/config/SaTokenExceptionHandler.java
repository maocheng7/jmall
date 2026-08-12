package com.jmall.common.security.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.jmall.common.core.result.Result;
import com.jmall.common.core.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Sa-Token 异常处理
 * <p>
 * 统一处理 Sa-Token 抛出的鉴权异常，转换为标准 {@link Result} 响应。
 * </p>
 *
 * @author jmall
 */
@Slf4j
@RestControllerAdvice
public class SaTokenExceptionHandler {

    /**
     * 未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLogin(NotLoginException e) {
        log.warn("未登录: type={}, msg={}", e.getType(), e.getMessage());
        return Result.fail(ResultCode.UNAUTHORIZED);
    }

    /**
     * 无权限异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result<Void> handleNotPermission(NotPermissionException e) {
        log.warn("无权限: code={}", e.getCode());
        return Result.fail(ResultCode.FORBIDDEN);
    }

    /**
     * 角色不符异常
     */
    @ExceptionHandler(NotRoleException.class)
    public Result<Void> handleNotRole(NotRoleException e) {
        log.warn("角色不符: role={}", e.getRole());
        return Result.fail(ResultCode.FORBIDDEN);
    }
}
