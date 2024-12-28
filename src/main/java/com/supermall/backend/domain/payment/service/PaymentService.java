package com.supermall.backend.domain.payment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.payment.dto.PaymentRequest;
import com.supermall.backend.domain.payment.dto.PaymentResponse;
import com.supermall.backend.domain.payment.entity.Payment;

public interface PaymentService {
    PaymentResponse createPayment(Integer userId, PaymentRequest request);
    PaymentResponse getPayment(Integer userId, Integer paymentId);
    Page<PaymentResponse> getUserPayments(Integer userId, int page, int size);
    void handlePaymentCallback(String paymentNo, String transactionId, Payment.Status status, String failureReason);
    PaymentResponse requestRefund(Integer userId, Integer paymentId);
    PaymentResponse getRefundStatus(Integer userId, Integer paymentId);
} 