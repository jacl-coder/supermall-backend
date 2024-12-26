package com.supermall.backend.domain.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

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
    private String role;
    
    @TableField("created_time")
    private LocalDateTime createdTime;
    
    @TableField("updated_time")
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
} 