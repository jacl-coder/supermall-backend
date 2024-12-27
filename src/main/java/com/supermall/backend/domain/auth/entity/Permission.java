package com.supermall.backend.domain.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("permissions")
public class Permission {
    @TableId(value = "permission_id", type = IdType.AUTO)
    private Integer id;
    
    private String name;
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 