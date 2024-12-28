package com.supermall.backend.domain.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.notification.entity.Notification;
import com.supermall.backend.domain.notification.entity.Notification.NotificationType;
import com.supermall.backend.domain.notification.entity.Notification.NotificationStatus;
import com.supermall.backend.domain.notification.entity.Notification.Priority;
import com.supermall.backend.domain.notification.mapper.NotificationMapper;
import com.supermall.backend.domain.notification.dto.NotificationResponse;
import com.supermall.backend.domain.notification.service.NotificationService;
import com.supermall.backend.domain.order.service.OrderService;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.entity.ReturnOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import org.springframework.context.annotation.Lazy;

@Slf4j
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final OrderService orderService;

    public NotificationServiceImpl(@Lazy OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    @Transactional
    public NotificationResponse createNotification(Integer userId, String title, String content,
                                                NotificationType type, Integer referenceId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setIsRead(false);
        
        save(notification);
        
        NotificationResponse response = new NotificationResponse();
        BeanUtils.copyProperties(notification, response);
        return response;
    }

    @Override
    public Page<NotificationResponse> getUserNotifications(Integer userId, Boolean unreadOnly, int page, int size) {
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<Notification>()
            .eq(Notification::getUserId, userId)
            .eq(unreadOnly != null && unreadOnly, Notification::getIsRead, false)
            .orderByDesc(Notification::getCreatedAt);
        
        Page<Notification> notificationPage = page(new Page<>(page, size), queryWrapper);
        
        Page<NotificationResponse> responsePage = new Page<>(
            notificationPage.getCurrent(),
            notificationPage.getSize(),
            notificationPage.getTotal()
        );
        
        responsePage.setRecords(notificationPage.getRecords().stream()
            .map(notification -> {
                NotificationResponse response = new NotificationResponse();
                BeanUtils.copyProperties(notification, response);
                return response;
            })
            .toList());
        
        return responsePage;
    }

    @Override
    public NotificationResponse getNotification(Integer notificationId, Integer userId) {
        Notification notification = getById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new BusinessException("通知不存在或无权限访问");
        }
        
        NotificationResponse response = new NotificationResponse();
        BeanUtils.copyProperties(notification, response);
        return response;
    }

    @Override
    @Transactional
    public void markAsRead(Integer notificationId, Integer userId) {
        update(new LambdaUpdateWrapper<Notification>()
            .eq(Notification::getId, notificationId)
            .eq(Notification::getUserId, userId)
            .set(Notification::getIsRead, true));
    }

    @Override
    @Transactional
    public void markAllAsRead(Integer userId) {
        update(new LambdaUpdateWrapper<Notification>()
            .eq(Notification::getUserId, userId)
            .eq(Notification::getIsRead, false)
            .set(Notification::getIsRead, true));
    }

    @Override
    public Long getUnreadCount(Integer userId) {
        return count(new LambdaQueryWrapper<Notification>()
            .eq(Notification::getUserId, userId)
            .eq(Notification::getIsRead, false));
    }

    @Async("notificationExecutor")
    @Override
    public void sendOrderStatusNotification(Integer orderId, String status, String content) {
        try {
            Order order = orderService.getOrder(orderId);
            if (order == null) {
                log.error("发送订单状态通知失败：订单不存在, orderId: {}", orderId);
                return;
            }

            NotificationType type = getOrderNotificationType(status);
            String title = type.getDescription();

            Notification notification = new Notification();
            notification.setUserId(order.getUserId());
            notification.setTitle(title);
            notification.setContent(content);
            notification.setType(type);
            notification.setReferenceId(orderId);
            notification.setIsRead(false);
            notification.setStatus(NotificationStatus.PENDING);
            notification.setPriority(Priority.MEDIUM);
            notification.setExpireTime(LocalDateTime.now().plusDays(30));

            save(notification);
            log.info("发送订单状态通知成功，订单号: {}, 状态: {}", order.getOrderNo(), status);
        } catch (Exception e) {
            log.error("发送订单状态通知失败", e);
        }
    }

    @Async("notificationExecutor")
    @Override
    public void sendReturnOrderStatusNotification(Integer returnId, String status, String content) {
        try {
            ReturnOrder returnOrder = orderService.getReturnOrder(returnId);
            if (returnOrder == null) {
                log.error("发送退货状态通知失败：退货单不存在, returnId: {}", returnId);
                return;
            }

            NotificationType type = getReturnNotificationType(status);
            String title = type.getDescription();

            Notification notification = new Notification();
            notification.setUserId(returnOrder.getUserId());
            notification.setTitle(title);
            notification.setContent(content);
            notification.setType(type);
            notification.setReferenceId(returnId);
            notification.setIsRead(false);
            notification.setStatus(NotificationStatus.PENDING);
            notification.setPriority(Priority.MEDIUM);
            notification.setExpireTime(LocalDateTime.now().plusDays(30));

            save(notification);
            log.info("发送退货状态通知成功，退货单号: {}, 状态: {}", returnId, status);
        } catch (Exception e) {
            log.error("发送退货状态通知失败", e);
        }
    }

    @Async("notificationExecutor")
    @Override
    public void sendPaymentStatusNotification(Integer orderId, boolean success, String message) {
        try {
            Order order = orderService.getOrder(orderId);
            if (order == null) {
                log.error("发送支付状态通知失败：订单不存在, orderId: {}", orderId);
                return;
            }

            NotificationType type = success ? NotificationType.PAYMENT_SUCCESS : NotificationType.PAYMENT_FAILED;
            String title = type.getDescription();

            Notification notification = new Notification();
            notification.setUserId(order.getUserId());
            notification.setTitle(title);
            notification.setContent(message);
            notification.setType(type);
            notification.setReferenceId(orderId);
            notification.setIsRead(false);
            notification.setStatus(NotificationStatus.PENDING);
            notification.setPriority(Priority.HIGH);
            notification.setExpireTime(LocalDateTime.now().plusDays(30));

            save(notification);
            log.info("发送支付状态通知成功，订单号: {}, 状态: {}", order.getOrderNo(), success ? "成功" : "失败");
        } catch (Exception e) {
            log.error("发送支付状态通知失败", e);
        }
    }

    @Async("notificationExecutor")
    @Override
    public void sendRefundStatusNotification(Integer returnId, boolean success, String message) {
        try {
            ReturnOrder returnOrder = orderService.getReturnOrder(returnId);
            if (returnOrder == null) {
                log.error("发送退款状态通知失败：退货单不存在, returnId: {}", returnId);
                return;
            }

            NotificationType type = success ? NotificationType.REFUND_SUCCESS : NotificationType.REFUND_FAILED;
            String title = type.getDescription();

            Notification notification = new Notification();
            notification.setUserId(returnOrder.getUserId());
            notification.setTitle(title);
            notification.setContent(message);
            notification.setType(type);
            notification.setReferenceId(returnId);
            notification.setIsRead(false);
            notification.setStatus(NotificationStatus.PENDING);
            notification.setPriority(Priority.HIGH);
            notification.setExpireTime(LocalDateTime.now().plusDays(30));

            save(notification);
            log.info("发送退款状态通知成功，退货单号: {}, 状态: {}", returnId, success ? "成功" : "失败");
        } catch (Exception e) {
            log.error("发送退款状态通知失败", e);
        }
    }

    @Async("notificationExecutor")
    @Override
    public void sendSystemNotification(String title, String content, Integer[] userIds) {
        try {
            List<Notification> notifications = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expireTime = now.plusDays(30);

            for (Integer userId : userIds) {
                Notification notification = new Notification();
                notification.setUserId(userId);
                notification.setTitle(title);
                notification.setContent(content);
                notification.setType(NotificationType.SYSTEM_ANNOUNCEMENT);
                notification.setIsRead(false);
                notification.setStatus(NotificationStatus.PENDING);
                notification.setPriority(Priority.LOW);
                notification.setExpireTime(expireTime);
                notifications.add(notification);
            }

            saveBatch(notifications);
            log.info("发送系统通知成功，接收用户数: {}", userIds.length);
        } catch (Exception e) {
            log.error("发送系统通知失败", e);
        }
    }

    private NotificationType getOrderNotificationType(String status) {
        return switch (status) {
            case "created" -> NotificationType.ORDER_CREATED;
            case "paid" -> NotificationType.ORDER_PAID;
            case "shipped" -> NotificationType.ORDER_SHIPPED;
            case "completed" -> NotificationType.ORDER_COMPLETED;
            case "cancelled" -> NotificationType.ORDER_CANCELLED;
            default -> throw new BusinessException("未知的订单状态：" + status);
        };
    }

    private NotificationType getReturnNotificationType(String status) {
        return switch (status) {
            case "created" -> NotificationType.RETURN_CREATED;
            case "approved" -> NotificationType.RETURN_APPROVED;
            case "rejected" -> NotificationType.RETURN_REJECTED;
            case "completed" -> NotificationType.RETURN_COMPLETED;
            default -> throw new BusinessException("未知的退货状态：" + status);
        };
    }

    @Override
    public Page<NotificationResponse> getUserNotificationsByType(Integer userId, NotificationType type, int page, int size) {
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<Notification>()
            .eq(Notification::getUserId, userId)
            .eq(Notification::getType, type)
            .orderByDesc(Notification::getCreatedAt);
        
        Page<Notification> notificationPage = page(new Page<>(page, size), queryWrapper);
        
        Page<NotificationResponse> responsePage = new Page<>(
            notificationPage.getCurrent(),
            notificationPage.getSize(),
            notificationPage.getTotal()
        );
        
        responsePage.setRecords(notificationPage.getRecords().stream()
            .map(notification -> {
                NotificationResponse response = new NotificationResponse();
                BeanUtils.copyProperties(notification, response);
                return response;
            })
            .toList());
        
        return responsePage;
    }

    @Override
    @Transactional
    public void deleteNotification(Integer notificationId, Integer userId) {
        // 验证通知是否属于该用户
        Notification notification = getOne(new LambdaQueryWrapper<Notification>()
            .eq(Notification::getId, notificationId)
            .eq(Notification::getUserId, userId));
            
        if (notification == null) {
            throw new BusinessException("通知不存在或无权限删除");
        }
        
        removeById(notificationId);
        log.info("通知删除成功，通知ID: {}, 用户ID: {}", notificationId, userId);
    }

    @Override
    @Transactional
    public void batchDeleteNotifications(List<Integer> notificationIds, Integer userId) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return;
        }
        
        // 验证所有通知是否属于该用户
        List<Notification> notifications = list(new LambdaQueryWrapper<Notification>()
            .in(Notification::getId, notificationIds)
            .eq(Notification::getUserId, userId));
            
        if (notifications.size() != notificationIds.size()) {
            throw new BusinessException("部分通知不存在或无权限删除");
        }
        
        removeBatchByIds(notificationIds);
        log.info("批量删除通知成功，通知数量: {}, 用户ID: {}", notificationIds.size(), userId);
    }

    @Override
    @Transactional
    public void cleanExpiredNotifications() {
        LocalDateTime now = LocalDateTime.now();
        remove(new LambdaQueryWrapper<Notification>()
            .lt(Notification::getExpireTime, now));
        log.info("清理过期通知完成，当前时间: {}", now);
    }

    @Override
    @Transactional
    public void retryFailedNotifications() {
        // 获取所有发送失败的通知
        List<Notification> failedNotifications = list(new LambdaQueryWrapper<Notification>()
            .eq(Notification::getStatus, NotificationStatus.FAILED));
            
        if (failedNotifications.isEmpty()) {
            return;
        }
        
        for (Notification notification : failedNotifications) {
            try {
                // 更新通知状态为待发送
                notification.setStatus(NotificationStatus.PENDING);
                updateById(notification);
                log.info("重试发送通知，通知ID: {}", notification.getId());
            } catch (Exception e) {
                log.error("重试发送通知失败，通知ID: {}", notification.getId(), e);
            }
        }
    }

    @Override
    @Transactional
    public void batchCreateNotifications(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return;
        }
        
        // 设置通知的默认值
        LocalDateTime now = LocalDateTime.now();
        notifications.forEach(notification -> {
            notification.setCreatedAt(now);
            notification.setIsRead(false);
            notification.setStatus(NotificationStatus.PENDING);
            if (notification.getPriority() == null) {
                notification.setPriority(Priority.MEDIUM);
            }
            if (notification.getExpireTime() == null) {
                notification.setExpireTime(now.plusDays(30)); // 默认30天过期
            }
        });
        
        saveBatch(notifications);
        log.info("批量创建通知成功，通知数量: {}", notifications.size());
    }
} 