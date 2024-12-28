package com.supermall.backend.domain.log.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("system_logs")
public class SystemLog {
    @TableId(value = "log_id", type = IdType.AUTO)
    private Integer id;
    
    private Integer authId;
    private String module;
    private String action;
    private String detail;
    private String ipAddress;
    private String userAgent;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 