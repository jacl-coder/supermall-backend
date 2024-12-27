package com.supermall.backend.domain.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("role_permissions")
public class RolePermission {
    @TableId(value = "role_id")
    private Integer roleId;
    
    private Integer permissionId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 