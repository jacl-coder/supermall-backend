package com.supermall.backend.domain.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotNull(message = "订单ID不能为空")
    private Integer orderId;
    
    @NotNull(message = "支付方式不能为空")
    private String paymentMethod;
} 