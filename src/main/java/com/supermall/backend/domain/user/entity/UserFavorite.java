package com.supermall.backend.domain.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_favorites")
public class UserFavorite {
    @TableId(value = "favorite_id", type = IdType.AUTO)
    private Integer id;
    
    private Integer userId;
    private Integer productId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 