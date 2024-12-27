package com.supermall.backend.domain.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("auth_users")
public class AuthUser {
    @TableId(value = "auth_id", type = IdType.AUTO)
    private Long id;
    
    private String username;
    private String email;
    private String passwordHash;
    private Long roleId;
    
    @TableField("status")
    private String status; // active, inactive, locked
    
    private LocalDateTime lastLogin;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 