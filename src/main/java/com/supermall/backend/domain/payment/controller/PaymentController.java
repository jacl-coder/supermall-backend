package com.supermall.backend.domain.payment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.payment.dto.PaymentRequest;
import com.supermall.backend.domain.payment.dto.PaymentResponse;
import com.supermall.backend.domain.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        // TODO: 从SecurityContext获取用户ID
        Integer userId = 1;
        return Result.success(paymentService.createPayment(userId, request));
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('USER')")
    public Result<PaymentResponse> getPayment(@PathVariable Integer paymentId) {
        // TODO: 从SecurityContext获取用户ID
        Integer userId = 1;
        PaymentResponse payment = paymentService.getPayment(userId, paymentId);
        if (payment == null) {
            return Result.fail("支付记录不存在");
        }
        return Result.success(payment);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Result<Page<PaymentResponse>> getUserPayments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // TODO: 从SecurityContext获取用户ID
        Integer userId = 1;
        return Result.success(paymentService.getUserPayments(userId, page, size));
    }

    @PostMapping("/callback")
    public Result<Void> handlePaymentCallback(
            @RequestParam String paymentNo,
            @RequestParam String transactionId,
            @RequestParam String status,
            @RequestParam(required = false) String failureReason) {
        paymentService.handlePaymentCallback(paymentNo, transactionId, status, failureReason);
        return Result.success(null);
    }
} 