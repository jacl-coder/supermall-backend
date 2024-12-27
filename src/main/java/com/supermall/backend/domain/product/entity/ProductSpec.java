package com.supermall.backend.domain.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_specs")
public class ProductSpec {
    @TableId(value = "spec_id", type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String specName;
    private String specValue;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 