package com.supermall.backend.domain.notification.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.notification.dto.NotificationResponse;
import com.supermall.backend.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "消息通知管理", description = "包括系统消息推送、用户通知查询、消息状态更新等功能")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "获取通知列表")
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam(required = false) Boolean unreadOnly,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getUserNotifications(
                Long.valueOf(userDetails.getUsername()),
                unreadOnly,
                page,
                size
        ));
    }

    @Operation(summary = "获取通知详情")
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getNotification(
                notificationId,
                Long.valueOf(userDetails.getUsername())
        ));
    }

    @Operation(summary = "标记通知为已读")
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAsRead(
                notificationId,
                Long.valueOf(userDetails.getUsername())
        );
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "标记所有通知为已读")
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAllAsRead(
                Long.valueOf(userDetails.getUsername())
        );
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getUnreadCount(
                Long.valueOf(userDetails.getUsername())
        ));
    }
} 