package com.supermall.backend.domain.payment.exception;

import com.supermall.backend.common.exception.BusinessException;
import lombok.Getter;

@Getter
public class PaymentException extends BusinessException {
    
    private final ErrorCode errorCode;
    
    public PaymentException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public PaymentException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public enum ErrorCode {
        PAYMENT_NOT_FOUND("支付记录不存在"),
        PAYMENT_ALREADY_EXISTS("订单已存在支付记录"),
        INVALID_AMOUNT("无效的支付金额"),
        INVALID_STATUS_TRANSITION("无效的状态转换"),
        PAYMENT_TIMEOUT("支付超时"),
        REFUND_AMOUNT_EXCEEDED("退款金额超过原支付金额"),
        ORDER_NOT_COMPLETED("订单未完成,不能退款"),
        INVALID_OPERATION("无效的操作"),
        PAYMENT_PROCESSING_FAILED("支付处理失败");
        
        private final String message;
        
        ErrorCode(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
} 