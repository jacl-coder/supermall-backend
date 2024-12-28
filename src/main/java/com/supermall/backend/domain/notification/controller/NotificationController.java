package com.supermall.backend.domain.notification.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.notification.dto.NotificationResponse;
import com.supermall.backend.domain.notification.service.NotificationService;
import com.supermall.backend.domain.notification.entity.Notification.NotificationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "通知管理", description = "包括系统通知、订单通知等功能")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "获取通知列表")
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam(required = false) Boolean unreadOnly,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = Integer.valueOf(userDetails.getUsername());
        if (type != null) {
            return ResponseEntity.ok(notificationService.getUserNotificationsByType(userId, type, page, size));
        }
        return ResponseEntity.ok(notificationService.getUserNotifications(userId, unreadOnly, page, size));
    }

    @Operation(summary = "获取通知详情")
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotification(
            @PathVariable Integer notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getNotification(
                notificationId,
                Integer.valueOf(userDetails.getUsername())
        ));
    }

    @Operation(summary = "标记通知为已读")
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Integer notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAsRead(
                notificationId,
                Integer.valueOf(userDetails.getUsername())
        );
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "标记所有通知为已读")
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAllAsRead(
                Integer.valueOf(userDetails.getUsername())
        );
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getUnreadCount(
                Integer.valueOf(userDetails.getUsername())
        ));
    }
    
    @Operation(summary = "删除通知")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Integer notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.deleteNotification(
                notificationId,
                Integer.valueOf(userDetails.getUsername())
        );
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "批量删除通知")
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDeleteNotifications(
            @RequestBody List<Integer> notificationIds,
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.batchDeleteNotifications(
                notificationIds,
                Integer.valueOf(userDetails.getUsername())
        );
        return ResponseEntity.ok().build();
    }
} 