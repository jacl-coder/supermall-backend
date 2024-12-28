package com.supermall.backend.domain.payment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.payment.dto.PaymentCallback;
import com.supermall.backend.domain.payment.dto.PaymentRequest;
import com.supermall.backend.domain.payment.dto.PaymentResponse;
import com.supermall.backend.domain.payment.entity.Payment;

import java.math.BigDecimal;

public interface PaymentService extends IService<Payment> {
    /**
     * 创建支付记录
     */
    PaymentResponse createPayment(Integer userId, PaymentRequest request);
    
    /**
     * 获取支付记录
     */
    PaymentResponse getPayment(Integer userId, Integer paymentId);
    
    /**
     * 获取用户的支付记录列表
     */
    Page<PaymentResponse> getUserPayments(Integer userId, int page, int size);
    
    /**
     * 处理支付回调
     */
    void handlePaymentCallback(PaymentCallback callback);
    
    /**
     * 处理退款
     */
    void handleRefund(Integer returnId, BigDecimal amount);
} 