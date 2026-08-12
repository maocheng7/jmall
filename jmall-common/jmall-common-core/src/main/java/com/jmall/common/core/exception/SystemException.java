package com.jmall.common.core.exception;

import java.io.Serial;

/**
 * 系统异常
 * <p>
 * 不可预见的系统级错误（如数据库连接失败、网络超时等）使用此异常。
 * </p>
 *
 * @author jmall
 */
public class SystemException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
