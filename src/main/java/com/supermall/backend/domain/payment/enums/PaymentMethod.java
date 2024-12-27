package com.supermall.backend.domain.payment.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    ALIPAY(1, "支付宝"),
    WECHAT(2, "微信支付"),
    BANK_CARD(3, "银行卡");
    
    private final int code;
    private final String description;
    
    PaymentMethod(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public static PaymentMethod fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentMethod method : values()) {
            if (method.getCode() == code) {
                return method;
            }
        }
        return null;
    }
} 