package com.supermall.backend.domain.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.security.annotation.RequirePermission;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.common.security.util.SecurityUtil;
import com.supermall.backend.domain.order.dto.RefundRequest;
import com.supermall.backend.domain.order.dto.RefundResponse;
import com.supermall.backend.domain.order.enums.RefundStatus;
import com.supermall.backend.domain.order.service.OrderRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单退款")
@RestController
@RequestMapping("/api/orders/refunds")
@RequiredArgsConstructor
public class OrderRefundController {

    private final OrderRefundService refundService;
    private final SecurityUtil securityUtil;

    @Operation(summary = "申请退款")
    @PostMapping
    @RequirePermission(role = "USER")
    public Result<RefundResponse> createRefund(@RequestBody RefundRequest request) {
        return Result.success(refundService.createRefund(securityUtil.getCurrentUserId(), request));
    }

    @Operation(summary = "获取退款详情")
    @GetMapping("/{refundId}")
    @RequirePermission(role = "USER")
    public Result<RefundResponse> getRefundDetail(@PathVariable Integer refundId) {
        return Result.success(refundService.getRefundDetail(securityUtil.getCurrentUserId(), refundId));
    }

    @Operation(summary = "获取用户退款列表")
    @GetMapping("/user")
    @RequirePermission(role = "USER")
    public Result<Page<RefundResponse>> getUserRefunds(
            @Parameter(description = "退款状态") @RequestParam(required = false) RefundStatus status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        return Result.success(refundService.getUserRefunds(securityUtil.getCurrentUserId(), status, page, size));
    }

    @Operation(summary = "取消退款申请")
    @PostMapping("/{refundId}/cancel")
    @RequirePermission(role = "USER")
    public Result<Void> cancelRefund(@PathVariable Integer refundId) {
        refundService.cancelRefund(securityUtil.getCurrentUserId(), refundId);
        return Result.success();
    }

    @Operation(summary = "获取商家退款列表")
    @GetMapping("/merchant")
    @RequirePermission(role = "MERCHANT")
    public Result<Page<RefundResponse>> getMerchantRefunds(
            @Parameter(description = "退款状态") @RequestParam(required = false) RefundStatus status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        return Result.success(refundService.getMerchantRefunds(securityUtil.getCurrentUserId(), status, page, size));
    }

    @Operation(summary = "同意退款")
    @PostMapping("/{refundId}/approve")
    @RequirePermission(role = "MERCHANT")
    public Result<Void> approveRefund(
            @PathVariable Integer refundId,
            @Parameter(description = "回复内容") @RequestParam(required = false) String reply) {
        refundService.approveRefund(securityUtil.getCurrentUserId(), refundId, reply);
        return Result.success();
    }

    @Operation(summary = "拒绝退款")
    @PostMapping("/{refundId}/reject")
    @RequirePermission(role = "MERCHANT")
    public Result<Void> rejectRefund(
            @PathVariable Integer refundId,
            @Parameter(description = "拒绝原因") @RequestParam String reason) {
        refundService.rejectRefund(securityUtil.getCurrentUserId(), refundId, reason);
        return Result.success();
    }
} 