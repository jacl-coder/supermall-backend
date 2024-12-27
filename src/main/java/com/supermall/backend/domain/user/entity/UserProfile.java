package com.supermall.backend.domain.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_profiles")
public class UserProfile {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long id;
    private Long authId;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 