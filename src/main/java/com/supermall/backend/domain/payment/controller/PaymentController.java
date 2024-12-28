package com.supermall.backend.domain.payment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.common.security.annotation.RequirePermission;
import com.supermall.backend.common.security.model.SecurityUser;
import com.supermall.backend.domain.payment.dto.PaymentRequest;
import com.supermall.backend.domain.payment.dto.PaymentResponse;
import com.supermall.backend.domain.payment.entity.Payment;
import com.supermall.backend.domain.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "支付管理", description = "支付模块相关接口，包括订单支付处理、退款管理、支付状态查询、支付记录管理等支付系统核心功能")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "创建支付订单")
    @PostMapping
    @RequirePermission(role = "USER")
    public Result<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(paymentService.createPayment(user.getId(), request));
    }

    @Operation(summary = "查询支付订单")
    @GetMapping("/{paymentId}")
    @RequirePermission(role = "USER")
    public Result<PaymentResponse> getPayment(
            @PathVariable Integer paymentId,
            @AuthenticationPrincipal SecurityUser user) {
        PaymentResponse payment = paymentService.getPayment(user.getId(), paymentId);
        if (payment == null) {
            return Result.fail("支付记录不存在");
        }
        return Result.success(payment);
    }

    @Operation(summary = "查询用户支付记录")
    @GetMapping
    @RequirePermission(role = "USER")
    public Result<Page<PaymentResponse>> getUserPayments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(paymentService.getUserPayments(user.getId(), page, size));
    }

    @Operation(summary = "支付回调处理")
    @PostMapping("/callback")
    public Result<Void> handlePaymentCallback(
            @RequestParam String paymentNo,
            @RequestParam String transactionId,
            @RequestParam Payment.Status status,
            @RequestParam(required = false) String failureReason) {
        paymentService.handlePaymentCallback(paymentNo, transactionId, status, failureReason);
        return Result.success(null);
    }

    @Operation(summary = "申请退款")
    @PostMapping("/{paymentId}/refund")
    @RequirePermission(role = "USER")
    public Result<PaymentResponse> requestRefund(
            @PathVariable Integer paymentId,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(paymentService.requestRefund(user.getId(), paymentId));
    }

    @Operation(summary = "查询退款状态")
    @GetMapping("/{paymentId}/refund")
    @RequirePermission(role = "USER")
    public Result<PaymentResponse> getRefundStatus(
            @PathVariable Integer paymentId,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(paymentService.getRefundStatus(user.getId(), paymentId));
    }
} 