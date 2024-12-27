package com.supermall.backend.domain.payment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String paymentNo;
    private String paymentMethod;
    private BigDecimal paymentAmount;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
} 