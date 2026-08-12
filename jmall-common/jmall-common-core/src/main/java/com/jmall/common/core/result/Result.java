package com.jmall.common.core.result;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应结果封装
 * <p>
 * 所有接口统一返回此格式：{@code { code, message, data }}
 * </p>
 *
 * @param <T> 数据类型
 * @author jmall
 */
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功返回 - 无数据
     *
     * @return 统一响应结果
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回 - 带数据
     *
     * @param data 响应数据
     * @return 统一响应结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回 - 带提示信息和数据
     *
     * @param message 提示信息
     * @param data    响应数据
     * @return 统一响应结果
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回 - 指定状态码枚举
     *
     * @param resultCode 状态码枚举
     * @return 统一响应结果
     */
    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 失败返回 - 指定状态码枚举和自定义提示
     *
     * @param resultCode 状态码枚举
     * @param message    自定义提示信息
     * @return 统一响应结果
     */
    public static <T> Result<T> fail(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    /**
     * 失败返回 - 指定状态码和提示
     *
     * @param code    状态码
     * @param message 提示信息
     * @return 统一响应结果
     */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败返回 - 仅提示信息（状态码默认500）
     *
     * @param message 提示信息
     * @return 统一响应结果
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message, null);
    }

    /**
     * 判断是否成功
     *
     * @return true=成功
     */
    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }
}
