package com.supermall.backend.domain.notification.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.notification.dto.NotificationResponse;
import com.supermall.backend.domain.notification.entity.Notification;
import com.supermall.backend.domain.notification.entity.Notification.NotificationType;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    
    /**
     * 创建通知
     */
    NotificationResponse createNotification(Integer userId, String title, String content,
                                         NotificationType type, Integer referenceId);
    
    /**
     * 获取用户通知列表
     */
    Page<NotificationResponse> getUserNotifications(Integer userId, Boolean unreadOnly,
                                                  int page, int size);
    
    /**
     * 按类型获取用户通知列表
     */
    Page<NotificationResponse> getUserNotificationsByType(Integer userId, NotificationType type,
                                                        int page, int size);
    
    /**
     * 获取通知详情
     */
    NotificationResponse getNotification(Integer notificationId, Integer userId);
    
    /**
     * 标记通知为已读
     */
    void markAsRead(Integer notificationId, Integer userId);
    
    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(Integer userId);
    
    /**
     * 获取未读通知数量
     */
    Long getUnreadCount(Integer userId);
    
    /**
     * 删除通知
     */
    void deleteNotification(Integer notificationId, Integer userId);
    
    /**
     * 批量删除通知
     */
    void batchDeleteNotifications(List<Integer> notificationIds, Integer userId);
    
    /**
     * 清理过期通知
     */
    void cleanExpiredNotifications();
    
    /**
     * 重试发送失败的通知
     */
    void retryFailedNotifications();
    
    /**
     * 订单状态变更通知
     */
    void sendOrderStatusNotification(Integer orderId, String status, String content);
    
    /**
     * 发送退货订单状态通知
     */
    void sendReturnOrderStatusNotification(Integer returnId, String status, String content);
    
    /**
     * 支付状态通知
     */
    void sendPaymentStatusNotification(Integer orderId, boolean success, String message);
    
    /**
     * 退款状态通知
     */
    void sendRefundStatusNotification(Integer returnId, boolean success, String message);
    
    /**
     * 系统通知
     */
    void sendSystemNotification(String title, String content, Integer[] userIds);
    
    /**
     * 批量创建通知
     */
    void batchCreateNotifications(List<Notification> notifications);
} 