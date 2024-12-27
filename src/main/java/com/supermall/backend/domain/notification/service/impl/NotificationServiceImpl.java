package com.supermall.backend.domain.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.notification.entity.Notification;
import com.supermall.backend.domain.notification.entity.Notification.NotificationType;
import com.supermall.backend.domain.notification.mapper.NotificationMapper;
import com.supermall.backend.domain.notification.dto.NotificationResponse;
import com.supermall.backend.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    @Transactional
    public NotificationResponse createNotification(Long userId, String title, String content,
                                                NotificationType type, Long referenceId) {
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
    public Page<NotificationResponse> getUserNotifications(Long userId, Boolean unreadOnly, int page, int size) {
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
    public NotificationResponse getNotification(Long notificationId, Long userId) {
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
    public void markAsRead(Long notificationId, Long userId) {
        update(new LambdaUpdateWrapper<Notification>()
            .eq(Notification::getId, notificationId)
            .eq(Notification::getUserId, userId)
            .set(Notification::getIsRead, true));
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        update(new LambdaUpdateWrapper<Notification>()
            .eq(Notification::getUserId, userId)
            .eq(Notification::getIsRead, false)
            .set(Notification::getIsRead, true));
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return count(new LambdaQueryWrapper<Notification>()
            .eq(Notification::getUserId, userId)
            .eq(Notification::getIsRead, false));
    }
} 