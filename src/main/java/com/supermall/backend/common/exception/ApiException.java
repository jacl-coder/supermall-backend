package com.supermall.backend.common.exception;

import com.supermall.backend.common.api.ResultCode;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ResultCode errorCode;

    public ApiException(String message) {
        super(message);
        this.errorCode = ResultCode.FAILED;
    }

    public ApiException(String message, ResultCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ResultCode.FAILED;
    }

    public ApiException(ResultCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
} 