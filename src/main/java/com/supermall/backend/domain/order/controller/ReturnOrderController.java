package com.supermall.backend.domain.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.security.aspect.RequirePermission;
import com.supermall.backend.domain.order.dto.ReturnOrderRequest;
import com.supermall.backend.domain.order.dto.ReturnOrderResponse;
import com.supermall.backend.domain.order.service.ReturnOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "退货管理", description = "退货相关接口")
@RestController
@RequestMapping("/api/v1/returns")
@RequiredArgsConstructor
public class ReturnOrderController {

    private final ReturnOrderService returnOrderService;

    @Data
    public static class HandleReturnRequest {
        private boolean approved;
        private String handlingNotes;
    }

    @Operation(summary = "申请退货")
    @PostMapping
    public ResponseEntity<ReturnOrderResponse> createReturnOrder(
            @RequestBody @Valid ReturnOrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(returnOrderService.createReturnOrder(
                request,
                Long.valueOf(userDetails.getUsername())
        ));
    }

    @Operation(summary = "获取退货订单详情")
    @GetMapping("/{returnId}")
    public ResponseEntity<ReturnOrderResponse> getReturnOrder(
            @PathVariable Long returnId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(returnOrderService.getReturnOrder(
                returnId,
                Long.valueOf(userDetails.getUsername())
        ));
    }

    @Operation(summary = "获取用户退货订单列表")
    @GetMapping("/user")
    public ResponseEntity<Page<ReturnOrderResponse>> getUserReturnOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(returnOrderService.getUserReturnOrders(
                Long.valueOf(userDetails.getUsername()),
                page,
                size
        ));
    }

    @Operation(summary = "获取商家退货订单列表")
    @RequirePermission("order:manage")
    @GetMapping("/merchant")
    public ResponseEntity<Page<ReturnOrderResponse>> getMerchantReturnOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(returnOrderService.getMerchantReturnOrders(
                Long.valueOf(userDetails.getUsername()),
                page,
                size
        ));
    }

    @Operation(summary = "商家处理退货申请")
    @RequirePermission("order:manage")
    @PostMapping("/{returnId}/handle")
    public ResponseEntity<ReturnOrderResponse> handleReturnOrder(
            @PathVariable Long returnId,
            @RequestBody @Valid HandleReturnRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(returnOrderService.handleReturnOrder(
                returnId,
                request.isApproved(),
                request.getHandlingNotes(),
                Long.valueOf(userDetails.getUsername())
        ));
    }

    @Operation(summary = "确认收到退货")
    @RequirePermission("order:manage")
    @PostMapping("/{returnId}/confirm-return")
    public ResponseEntity<ReturnOrderResponse> confirmReturn(
            @PathVariable Long returnId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(returnOrderService.confirmReturn(
                returnId,
                Long.valueOf(userDetails.getUsername())
        ));
    }

    @Operation(summary = "确认退款完成")
    @RequirePermission("order:manage")
    @PostMapping("/{returnId}/confirm-refund")
    public ResponseEntity<ReturnOrderResponse> confirmRefund(
            @PathVariable Long returnId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(returnOrderService.confirmRefund(
                returnId,
                Long.valueOf(userDetails.getUsername())
        ));
    }
} 