package com.supermall.backend.service.impl;

import com.supermall.backend.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Override
    public void sendOrderDeliveryNotification(Long userId, String orderNo, 
            String deliveryCompany, String deliverySn) {
        log.info("[订单发货通知] 用户ID: {}, 订单号: {}, 物流公司: {}, 物流单号: {}", 
                userId, orderNo, deliveryCompany, deliverySn);
    }

    @Override
    public void sendOrderCompletedNotification(Long userId, String orderNo) {
        log.info("[订单完成通知] 用户ID: {}, 订单号: {}", userId, orderNo);
    }

    @Override
    public void sendOrderCancelledNotification(Long userId, String orderNo) {
        log.info("[订单取消通知] 用户ID: {}, 订单号: {}", userId, orderNo);
    }
} 