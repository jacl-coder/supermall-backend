package com.supermall.backend.domain.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("role_permissions")
@KeySequence("role_permissions_seq")
public class RolePermission {
    @TableId(value = "role_id", type = IdType.INPUT)
    private Long roleId;
    
    @TableField("permission_id")
    private Long permissionId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 