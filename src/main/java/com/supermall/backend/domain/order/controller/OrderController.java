package com.supermall.backend.domain.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.order.dto.OrderCreateRequest;
import com.supermall.backend.domain.order.dto.OrderResponse;
import com.supermall.backend.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        // TODO: 从SecurityContext获取用户ID
        Long userId = 1L;
        return Result.success(orderService.createOrder(userId, request));
    }

    @GetMapping("/{orderId}")
    public Result<OrderResponse> getOrderDetail(@PathVariable Long orderId) {
        Long userId = 1L;
        OrderResponse order = orderService.getOrderDetail(userId, orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        return Result.success(order);
    }

    @GetMapping
    public Result<Page<OrderResponse>> getUserOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = 1L;
        return Result.success(orderService.getUserOrders(userId, status, page, size));
    }

    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        Long userId = 1L;
        orderService.cancelOrder(userId, orderId);
        return Result.success(null);
    }

    @PostMapping("/{orderId}/confirm")
    public Result<Void> confirmReceived(@PathVariable Long orderId) {
        Long userId = 1L;
        orderService.confirmReceived(userId, orderId);
        return Result.success(null);
    }
} 