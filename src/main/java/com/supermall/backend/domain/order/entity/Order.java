package com.supermall.backend.domain.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal paymentAmount;
    private BigDecimal shippingFee;
    
    @TableField("`status`")  // status是MySQL关键字，需要转义
    private String status;
    
    private LocalDateTime paymentTime;
    private LocalDateTime shippingTime;
    private LocalDateTime completionTime;
    private String addressSnapshot;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 