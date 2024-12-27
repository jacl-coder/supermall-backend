package com.supermall.backend.domain.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_items")
public class OrderItem {
    @TableId(value = "item_id", type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long productId;
    private Long merchantId;
    @TableField(value = "product_snapshot")
    private String productSnapshot;
    private Integer quantity;
    private BigDecimal price;
    
    @TableField(exist = false)
    private String productName;
    
    @TableField(exist = false)
    private String productImage;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 