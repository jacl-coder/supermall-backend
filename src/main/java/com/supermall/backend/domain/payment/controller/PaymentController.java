package com.supermall.backend.domain.payment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.payment.dto.PaymentCallback;
import com.supermall.backend.domain.payment.dto.PaymentRequest;
import com.supermall.backend.domain.payment.dto.PaymentResponse;
import com.supermall.backend.domain.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "支付管理", description = "包括支付创建、查询、回调处理等功能")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "创建支付记录")
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(paymentService.createPayment(
                Integer.valueOf(userDetails.getUsername()),
                request
        ));
    }

    @Operation(summary = "获取支付记录")
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Integer paymentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(paymentService.getPayment(
                Integer.valueOf(userDetails.getUsername()),
                paymentId
        ));
    }

    @Operation(summary = "获取支付记录列表")
    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getPayments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(paymentService.getUserPayments(
                Integer.valueOf(userDetails.getUsername()),
                page,
                size
        ));
    }

    @Operation(summary = "处理支付回调")
    @PostMapping("/callback")
    public ResponseEntity<Void> handleCallback(@RequestBody PaymentCallback callback) {
        paymentService.handlePaymentCallback(callback);
        return ResponseEntity.ok().build();
    }
} 