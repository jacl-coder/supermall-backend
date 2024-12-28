package com.supermall.backend.domain.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.common.security.annotation.RequirePermission;
import com.supermall.backend.common.security.model.SecurityUser;
import com.supermall.backend.domain.order.dto.OrderCreateRequest;
import com.supermall.backend.domain.order.dto.OrderResponse;
import com.supermall.backend.domain.order.enums.OrderStatus;
import com.supermall.backend.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理", description = "包括订单创建、查询、修改、删除等订单全生命周期管理功能")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping
    @RequirePermission(role = "USER")
    public Result<OrderResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(orderService.createOrder(user.getId(), request));
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderId}")
    @RequirePermission(role = "USER")
    public Result<OrderResponse> getOrderDetail(
            @PathVariable Integer orderId,
            @AuthenticationPrincipal SecurityUser user) {
        OrderResponse order = orderService.getOrderDetail(user.getId(), orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        return Result.success(order);
    }

    @Operation(summary = "获取用户订单列表")
    @GetMapping
    @RequirePermission(role = "USER")
    public Result<Page<OrderResponse>> getUserOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(orderService.getUserOrders(user.getId(), status, page, size));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{orderId}/cancel")
    @RequirePermission(role = "USER")
    public Result<Void> cancelOrder(
            @PathVariable Integer orderId,
            @AuthenticationPrincipal SecurityUser user) {
        orderService.cancelOrder(user.getId(), orderId);
        return Result.success(null);
    }

    @Operation(summary = "确认收货")
    @PostMapping("/{orderId}/confirm")
    @RequirePermission(role = "USER")
    public Result<Void> confirmReceived(
            @PathVariable Integer orderId,
            @AuthenticationPrincipal SecurityUser user) {
        orderService.confirmReceived(user.getId(), orderId);
        return Result.success(null);
    }

    @Operation(summary = "获取商家订单列表")
    @GetMapping("/merchant/{merchantId}")
    @RequirePermission(role = "MERCHANT")
    public Result<Page<OrderResponse>> getMerchantOrders(
            @PathVariable Integer merchantId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal SecurityUser user) {
        if (!user.getMerchantId().equals(merchantId)) {
            return Result.fail("无权查看其他商家的订单");
        }
        return Result.success(orderService.getMerchantOrders(merchantId, status, page, size));
    }

    @Operation(summary = "订单发货")
    @PostMapping("/{orderId}/ship")
    @RequirePermission(role = "MERCHANT")
    public Result<Void> shipOrder(
            @PathVariable Integer orderId,
            @AuthenticationPrincipal SecurityUser user) {
        orderService.shipOrder(user.getMerchantId(), orderId);
        return Result.success(null);
    }
} 