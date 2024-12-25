package com.supermall.backend.service;

public interface NotificationService {
    void sendOrderDeliveryNotification(Long userId, String orderNo, String deliveryCompany, String deliverySn);
    void sendOrderCompletedNotification(Long userId, String orderNo);
    void sendOrderCancelledNotification(Long userId, String orderNo);
} 