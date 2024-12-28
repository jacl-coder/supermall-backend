package com.supermall.backend.domain.cart.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("shopping_cart_items")
public class CartItem {
    @TableId(value = "item_id", type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private Boolean selected;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 