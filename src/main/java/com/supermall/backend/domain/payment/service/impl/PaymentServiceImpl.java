package com.supermall.backend.domain.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.enums.OrderStatus;
import com.supermall.backend.domain.order.service.OrderService;
import com.supermall.backend.domain.payment.dto.PaymentRequest;
import com.supermall.backend.domain.payment.dto.PaymentResponse;
import com.supermall.backend.domain.payment.entity.Payment;
import com.supermall.backend.domain.payment.mapper.PaymentMapper;
import com.supermall.backend.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    private final OrderService orderService;

    @Override
    @Transactional
    public PaymentResponse createPayment(Integer userId, PaymentRequest request) {
        // 验证订单
        Order order = orderService.getOrder(request.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 检查是否已存在支付记录
        Payment existingPayment = getOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getOrderId, request.getOrderId())
                .ne(Payment::getStatus, Payment.Status.FAILED)
                .ne(Payment::getStatus, Payment.Status.CLOSED));
                
        if (existingPayment != null) {
            throw new BusinessException("订单已存在支付记录");
        }

        // 创建支付记录
        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderId(request.getOrderId());
        payment.setUserId(userId);
        payment.setAmount(order.getPaymentAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(Payment.Status.PENDING);
        
        save(payment);
        log.info("创建支付记录成功，支付单号: {}", payment.getPaymentNo());
        
        return convertToResponse(payment);
    }

    @Override
    public PaymentResponse getPayment(Integer userId, Integer paymentId) {
        Payment payment = getOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getId, paymentId)
                .eq(Payment::getUserId, userId));
                
        return payment != null ? convertToResponse(payment) : null;
    }

    @Override
    public Page<PaymentResponse> getUserPayments(Integer userId, int page, int size) {
        Page<Payment> paymentPage = page(new Page<>(page, size),
                new LambdaQueryWrapper<Payment>()
                        .eq(Payment::getUserId, userId)
                        .orderByDesc(Payment::getCreatedAt));
                        
        return convertToResponsePage(paymentPage);
    }

    @Override
    @Transactional
    public void handlePaymentCallback(String paymentNo, String transactionId, Payment.Status status, String failureReason) {
        Payment payment = getOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getPaymentNo, paymentNo));
                
        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }

        payment.setTransactionId(transactionId);
        payment.setStatus(status);
        payment.setFailureReason(failureReason);
        
        if (status == Payment.Status.SUCCESS) {
            payment.setPaidAt(LocalDateTime.now());
            // 更新订单状态
            orderService.updateOrderStatus(payment.getOrderId(), OrderStatus.PAID, LocalDateTime.now());
        }
        
        updateById(payment);
        log.info("支付回调处理成功，支付单号: {}, 状态: {}", paymentNo, status);
    }

    @Override
    @Transactional
    public PaymentResponse requestRefund(Integer userId, Integer paymentId) {
        Payment payment = getOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getId, paymentId)
                .eq(Payment::getUserId, userId));
                
        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }
        
        if (payment.getStatus() != Payment.Status.SUCCESS) {
            throw new BusinessException("只有支付成功的订单可以申请退款");
        }

        payment.setStatus(Payment.Status.REFUND_PENDING);
        updateById(payment);
        log.info("申请退款成功，支付单号: {}", payment.getPaymentNo());
        
        return convertToResponse(payment);
    }

    @Override
    public PaymentResponse getRefundStatus(Integer userId, Integer paymentId) {
        Payment payment = getOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getId, paymentId)
                .eq(Payment::getUserId, userId));
                
        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }
        
        if (payment.getStatus() != Payment.Status.REFUND_PENDING 
            && payment.getStatus() != Payment.Status.REFUNDED) {
            throw new BusinessException("订单未申请退款");
        }
        
        return convertToResponse(payment);
    }

    private String generatePaymentNo() {
        return "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private PaymentResponse convertToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setPaymentNo(payment.getPaymentNo());
        response.setOrderId(payment.getOrderId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setTransactionId(payment.getTransactionId());
        response.setStatus(payment.getStatus());
        response.setFailureReason(payment.getFailureReason());
        response.setPaidAt(payment.getPaidAt());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }

    private Page<PaymentResponse> convertToResponsePage(Page<Payment> page) {
        Page<PaymentResponse> responsePage = new Page<>();
        responsePage.setRecords(page.getRecords().stream()
                .map(this::convertToResponse)
                .toList());
        responsePage.setTotal(page.getTotal());
        responsePage.setCurrent(page.getCurrent());
        responsePage.setSize(page.getSize());
        return responsePage;
    }
} 