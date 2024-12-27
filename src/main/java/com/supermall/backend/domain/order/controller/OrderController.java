package com.supermall.backend.domain.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.order.dto.OrderCreateRequest;
import com.supermall.backend.domain.order.dto.OrderResponse;
import com.supermall.backend.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理", description = "包括订单创建、查询、修改、删除等订单全生命周期管理功能")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        // TODO: 从SecurityContext获取用户ID
        Integer userId = 1;
        return Result.success(orderService.createOrder(userId, request));
    }

    @GetMapping("/{orderId}")
    public Result<OrderResponse> getOrderDetail(@PathVariable Integer orderId) {
        Integer userId = 1;
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
        Integer userId = 1;
        return Result.success(orderService.getUserOrders(userId, status, page, size));
    }

    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Integer orderId) {
        Integer userId = 1;
        orderService.cancelOrder(userId, orderId);
        return Result.success(null);
    }

    @PostMapping("/{orderId}/confirm")
    public Result<Void> confirmReceived(@PathVariable Integer orderId) {
        Integer userId = 1;
        orderService.confirmReceived(userId, orderId);
        return Result.success(null);
    }
} 