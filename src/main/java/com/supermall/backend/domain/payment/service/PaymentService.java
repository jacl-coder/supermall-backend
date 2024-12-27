package com.supermall.backend.domain.payment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.payment.dto.PaymentRequest;
import com.supermall.backend.domain.payment.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(Long userId, PaymentRequest request);
    PaymentResponse getPayment(Long userId, Long paymentId);
    Page<PaymentResponse> getUserPayments(Long userId, int page, int size);
    void handlePaymentCallback(String paymentNo, String transactionId, String status, String failureReason);
} 