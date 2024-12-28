package com.supermall.backend.domain.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_images")
public class ProductImage {
    @TableId(value = "image_id", type = IdType.AUTO)
    private Integer id;
    private Integer productId;
    private String imageUrl;
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 