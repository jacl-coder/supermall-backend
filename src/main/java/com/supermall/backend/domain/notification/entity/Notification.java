package com.supermall.backend.domain.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("notifications")
public class Notification {
    @TableId(value = "notification_id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String title;
    private String content;
    
    @TableField(value = "type")
    private NotificationType type;
    
    private Long referenceId;
    private Boolean isRead;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    public enum NotificationType {
        ORDER_STATUS,    // 订单状态变更
        RETURN_STATUS,   // 退货状态变更
        SYSTEM          // 系统通知
    }
} 