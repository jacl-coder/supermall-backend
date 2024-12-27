package com.supermall.backend.domain.payment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private String paymentNo;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionId;
    private String status;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;
    
    // 额外的展示信息
    private String orderNo;
    private String statusDesc;
} 