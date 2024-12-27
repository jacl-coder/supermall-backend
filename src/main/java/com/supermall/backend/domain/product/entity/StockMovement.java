package com.supermall.backend.domain.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("stock_movements")
public class StockMovement {
    @TableId(value = "movement_id", type = IdType.AUTO)
    private Integer id;
    
    private Integer productId;
    private Integer quantity;
    
    @TableField(value = "type")
    private MovementType type;
    
    private Integer referenceId;
    private Integer operatorId;
    private String notes;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    public enum MovementType {
        ORDER_CREATE,
        ORDER_CANCEL,
        RETURN,
        MANUAL_ADJUSTMENT
    }
} 