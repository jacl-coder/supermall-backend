package com.supermall.backend.domain.payment.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentCallback {
    private String paymentNo;
    private String transactionId;
    private boolean success;
    private String failureReason;
    private BigDecimal amount;
    private String paymentMethod;
    private String rawResponse;
} 