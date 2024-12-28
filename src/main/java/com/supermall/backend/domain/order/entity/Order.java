package com.supermall.backend.domain.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.supermall.backend.domain.order.enums.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    @TableId(value = "order_id", type = IdType.AUTO)
    private Integer id;
    private String orderNo;
    private Integer userId;
    private Integer merchantId;
    private BigDecimal totalAmount;
    private BigDecimal paymentAmount;
    private BigDecimal shippingFee;
    
    @TableField("`status`")
    private OrderStatus status;
    
    private LocalDateTime paymentTime;
    private LocalDateTime shippingTime;
    private LocalDateTime completionTime;
    private String addressSnapshot;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 