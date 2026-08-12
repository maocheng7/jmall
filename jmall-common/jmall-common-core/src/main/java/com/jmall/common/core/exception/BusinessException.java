package com.jmall.common.core.exception;

import com.jmall.common.core.result.ResultCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 业务异常
 * <p>
 * 所有可预见的业务错误均抛出此异常，由全局异常处理器捕获并转为统一响应。
 * </p>
 *
 * @author jmall
 */
@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 使用 ResultCode 枚举构造
     *
     * @param resultCode 状态码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * 使用 ResultCode 枚举 + 自定义提示构造
     *
     * @param resultCode 状态码枚举
     * @param message    自定义提示信息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    /**
     * 使用自定义错误码和提示构造
     *
     * @param code    错误码
     * @param message 提示信息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用提示信息构造（默认500错误码）
     *
     * @param message 提示信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.INTERNAL_SERVER_ERROR.getCode();
    }
}
