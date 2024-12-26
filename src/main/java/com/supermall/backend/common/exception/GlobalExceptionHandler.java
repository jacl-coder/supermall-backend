package com.supermall.backend.common.exception;

import com.supermall.backend.common.api.CommonResult;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult<Void> handleApiException(ApiException e) {
        log.error("API异常", e);
        return CommonResult.failed(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult<Void> handleValidException(MethodArgumentNotValidException e) {
        log.error("参数验证失败", e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return CommonResult.validateFailed(message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult<Void> handleValidException(BindException e) {
        log.error("参数绑定失败", e);
        FieldError fieldError = e.getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        return CommonResult.validateFailed(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult<Void> handleValidException(ConstraintViolationException e) {
        log.error("参数验证失败", e);
        String message = e.getConstraintViolations().iterator().next().getMessage();
        return CommonResult.validateFailed(message);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResult<Void> handleAuthenticationException(BadCredentialsException e) {
        log.error("认证失败", e);
        return CommonResult.unauthorized("用户名或密码错误");
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResult<Void> handleAuthenticationException(AuthenticationException e) {
        log.error("认证失败", e);
        return CommonResult.unauthorized(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonResult<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.error("没有权限访问", e);
        return CommonResult.forbidden("没有权限访问");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return CommonResult.failed("系统异常，请联系管理员");
    }
} 