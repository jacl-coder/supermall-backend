package com.supermall.backend.domain.payment.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING(0, "待支付"),
    SUCCESS(1, "支付成功"),
    FAILED(2, "支付失败"),
    CLOSED(3, "已关闭");
    
    private final int code;
    private final String description;
    
    PaymentStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public static PaymentStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
} 