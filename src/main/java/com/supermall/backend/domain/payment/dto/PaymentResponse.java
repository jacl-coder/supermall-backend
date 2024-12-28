package com.supermall.backend.domain.payment.dto;

import com.supermall.backend.domain.payment.entity.Payment;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Integer id;
    private String paymentNo;
    private Integer orderId;
    private BigDecimal amount;
    private Payment.PaymentMethod paymentMethod;
    private String transactionId;
    private Payment.Status status;
    private String failureReason;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
} 