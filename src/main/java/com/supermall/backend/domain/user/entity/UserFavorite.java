package com.supermall.backend.domain.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_favorites")
public class UserFavorite {
    @TableId(value = "favorite_id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long productId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 