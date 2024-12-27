package com.supermall.backend.domain.notification.dto;

import com.supermall.backend.domain.notification.entity.Notification.NotificationType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private NotificationType type;
    private Long referenceId;
    private Boolean isRead;
    private LocalDateTime createdAt;
} 