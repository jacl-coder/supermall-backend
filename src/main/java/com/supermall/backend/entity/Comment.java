package com.supermall.backend.entity;

import com.baomidou.mybatisplus.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long productId;
    
    private String content;
    
    private Integer rating;  // 评分 1-5
    
    private String images;  // 评论图片，多个图片用逗号分隔
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    private Long orderId;  // 添加订单ID字段
} 