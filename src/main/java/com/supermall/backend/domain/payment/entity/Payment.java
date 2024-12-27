package com.supermall.backend.domain.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payments")
public class Payment {
    @TableId(value = "payment_id", type = IdType.AUTO)
    private Long id;
    
    private String paymentNo;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionId;
    private String status;
    private String failureReason;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    private LocalDateTime paidAt;
} 