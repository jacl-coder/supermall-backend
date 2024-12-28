package com.supermall.backend.common.exception;

import com.supermall.backend.common.api.Result;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.sql.SQLSyntaxErrorException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.fail(errorMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String paramName = e.getName();
        String errorMessage;
        switch (paramName) {
            case "reviewId" -> errorMessage = "评论ID无效，请检查后重试";
            case "productId" -> errorMessage = "商品ID无效，请检查后重试";
            case "orderId" -> errorMessage = "订单ID无效，请检查后重试";
            case "merchantId" -> errorMessage = "商家ID无效，请检查后重试";
            case "userId" -> errorMessage = "用户ID无效，请检查后重试";
            case "categoryId" -> errorMessage = "分类ID无效，请检查后重试";
            case "addressId" -> errorMessage = "地址ID无效，请检查后重试";
            case "cartId" -> errorMessage = "购物车ID无效，请检查后重试";
            case "refundId" -> errorMessage = "退款ID无效，请检查后重试";
            case "returnId" -> errorMessage = "退货ID无效，请检查后重试";
            case "paymentId" -> errorMessage = "支付ID无效，请检查后重试";
            case "status" -> {
                String type = e.getValue() != null ? e.getValue().toString() : "";
                switch (type.toUpperCase()) {
                    case "ORDER" -> errorMessage = "订单状态���数无效";
                    case "PAYMENT" -> errorMessage = "支付状态参数无效";
                    case "REFUND" -> errorMessage = "退款状态参数无效";
                    case "RETURN" -> errorMessage = "退货状态参数无效";
                    case "MERCHANT" -> errorMessage = "商家状态参数无效";
                    case "PRODUCT" -> errorMessage = "商品状态参数无效";
                    default -> errorMessage = "状态参数无效，请检查后重试";
                }
            }
            default -> errorMessage = String.format("参数%s格式错误", paramName);
        }
        return Result.fail(errorMessage);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        String message = e.getCause().getMessage();
        String errorMessage;
        
        if (message.contains("uk_business_license")) {
            errorMessage = "营业执照号已被注册";
        } else if (message.contains("uk_shop_name")) {
            errorMessage = "店铺名称已被使用";
        } else if (message.contains("uk_username")) {
            errorMessage = "用户名已被注册";
        } else if (message.contains("uk_user_product")) {
            errorMessage = "该商品已在购物车中";
        } else if (message.contains("uk_order_no")) {
            errorMessage = "订单号重复，请重新提交";
        } else if (message.contains("uk_payment_no")) {
            errorMessage = "支付单号重复，请重新提交";
        } else if (message.contains("uk_refund_no")) {
            errorMessage = "退款单号重复，请重新提交";
        } else if (message.contains("uk_return_no")) {
            errorMessage = "退货单号重复，请重新提交";
        } else if (message.contains("uk_review_user_product")) {
            errorMessage = "您已经评价过该商品";
        } else {
            errorMessage = "数据已存在，请检查后重试";
        }
        
        return Result.fail(errorMessage);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        String errorMessage;
        if (e instanceof BadCredentialsException) {
            errorMessage = "用户名或密码错误";
        } else if (e instanceof DisabledException) {
            errorMessage = "账号已被禁用";
        } else if (e instanceof InsufficientAuthenticationException) {
            errorMessage = "请先登录";
        } else {
            errorMessage = "认证失败";
        }
        return Result.fail(errorMessage);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        return Result.fail("您没有权限执行此操作");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return Result.fail("请求参数格式错误");
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        String headerName = e.getHeaderName();
        if ("Authorization".equalsIgnoreCase(headerName)) {
            return Result.fail("请先登录");
        }
        return Result.fail(String.format("缺少请求头: %s", headerName));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return Result.fail(String.format("缺少必要的参数: %s", e.getParameterName()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return Result.fail("不支持的请求方法");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return Result.fail("上传文件大小超出限制");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = e.getMessage();
        if (message != null) {
            if (message.contains("foreign key constraint")) {
                if (message.contains("product_id")) {
                    return Result.fail("商品不存在或已被删除");
                } else if (message.contains("user_id")) {
                    return Result.fail("用户不存在或已被删除");
                } else if (message.contains("merchant_id")) {
                    return Result.fail("商家不存在或已被删除");
                } else if (message.contains("order_id")) {
                    return Result.fail("订单不存在或已被删除");
                }
                return Result.fail("关联数据不存在或已被删除");
            } else if (message.contains("Data too long")) {
                return Result.fail("输入数据超出长度限制");
            }
        }
        return Result.fail("数据完整性错误，请检查输入数据");
    }

    @ExceptionHandler(SQLSyntaxErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        return Result.fail("数据库操作错误，请联系管理员");
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleNumberFormatException(NumberFormatException e) {
        return Result.fail("参数格式错误，请输入有效的数字");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        // 记录未知异常的日志
        e.printStackTrace();
        return Result.fail("服务器内部错误，请稍后重试");
    }
} 