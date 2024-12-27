package com.supermall.backend.domain.notification.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.notification.entity.Notification;
import com.supermall.backend.domain.notification.entity.Notification.NotificationType;
import com.supermall.backend.domain.notification.dto.NotificationResponse;

public interface NotificationService {
    /**
     * 创建通知
     */
    NotificationResponse createNotification(Long userId, String title, String content, 
                                         NotificationType type, Long referenceId);
    
    /**
     * 获取用户的通知列表
     */
    Page<NotificationResponse> getUserNotifications(Long userId, Boolean unreadOnly, int page, int size);
    
    /**
     * 获取通知详情
     */
    NotificationResponse getNotification(Long notificationId, Long userId);
    
    /**
     * 标记通知为已读
     */
    void markAsRead(Long notificationId, Long userId);
    
    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(Long userId);
    
    /**
     * 获取未读通知数量
     */
    Long getUnreadCount(Long userId);
} 