package com.supermall.backend.common.api;

import lombok.Data;

/**
 * 通用返回结果
 */
@Data
public class CommonResult<T> {
    private long code;
    private String message;
    private T data;

    protected CommonResult() {
    }

    protected CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     */
    public static <T> CommonResult<T> success(String message, T data) {
        return new CommonResult<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 成功返回结果
     */
    public static <T> CommonResult<T> success() {
        return new CommonResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<>(ResultCode.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed() {
        return new CommonResult<>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMessage(), null);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> CommonResult<T> validateFailed() {
        return new CommonResult<>(ResultCode.VALIDATE_FAILED.getCode(), ResultCode.VALIDATE_FAILED.getMessage(), null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized(String message) {
        return new CommonResult<>(ResultCode.UNAUTHORIZED.getCode(), message, null);
    }

    /**
     * 未授权返回结果
     */
    public static <T> CommonResult<T> forbidden(String message) {
        return new CommonResult<>(ResultCode.FORBIDDEN.getCode(), message, null);
    }

    /**
     * 未授权返回结果
     */
    public static <T> CommonResult<T> forbidden() {
        return new CommonResult<>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized() {
        return new CommonResult<>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), null);
    }
} 