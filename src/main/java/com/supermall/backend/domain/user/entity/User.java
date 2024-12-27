package com.supermall.backend.domain.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.supermall.backend.domain.role.entity.Role;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    private String password;
    private String avatar;
    private String phone;
    private String email;
    private Integer status;
    
    @TableField(exist = false)
    private List<Role> roles;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
} 