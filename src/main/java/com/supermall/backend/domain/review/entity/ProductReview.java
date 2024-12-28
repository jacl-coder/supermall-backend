package com.supermall.backend.domain.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_reviews")
public class ProductReview {
    @TableId(value = "review_id", type = IdType.AUTO)
    private Integer id;
    
    @TableField("order_id")
    private Integer orderId;
    
    @TableField("order_item_id")
    private Integer orderItemId;
    
    @TableField("user_id")
    private Integer userId;
    
    @TableField("product_id")
    private Integer productId;
    
    @TableField("merchant_id")
    private Integer merchantId;
    
    private Integer rating;
    private String content;
    private String images;
    
    @TableField("`status`")
    private Status status;
    
    private String rejectReason;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public enum Status {
        PENDING,    // 待审核
        PUBLISHED,  // 已发布
        REJECTED,   // 已拒绝
        DELETED     // 已删除
    }
} 