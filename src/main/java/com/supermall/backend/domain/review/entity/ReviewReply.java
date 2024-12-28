package com.supermall.backend.domain.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("review_replies")
public class ReviewReply {
    @TableId(value = "reply_id", type = IdType.AUTO)
    private Integer id;
    
    @TableField("review_id")
    private Integer reviewId;
    
    @TableField("user_id")
    private Integer userId;
    
    @TableField("merchant_id")
    private Integer merchantId;
    
    private String content;
    
    @TableField("`status`")
    private Status status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public enum Status {
        PUBLISHED,  // 已发布
        DELETED     // 已删除
    }
} 