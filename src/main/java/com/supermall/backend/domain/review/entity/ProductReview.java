package com.supermall.backend.domain.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_reviews")
public class ProductReview {
    @TableId(value = "review_id", type = IdType.AUTO)
    private Long id;
    
    private Long orderItemId;
    private Long userId;
    private Long productId;
    private Long merchantId;
    private Integer rating;
    private String content;
    private String images;
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 