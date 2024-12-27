package com.supermall.backend.domain.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.service.OrderService;
import com.supermall.backend.domain.payment.dto.PaymentRequest;
import com.supermall.backend.domain.payment.dto.PaymentResponse;
import com.supermall.backend.domain.payment.entity.Payment;
import com.supermall.backend.domain.payment.mapper.PaymentMapper;
import com.supermall.backend.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    private final OrderService orderService;

    @Override
    @Transactional
    public PaymentResponse createPayment(Integer userId, PaymentRequest request) {
        // 检查订单是否存在且属于当前用户
        Order order = orderService.getOrder(request.getOrderId());
        if (order == null || !orderService.isUserOrder(userId, order.getId())) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态是否为待支付
        if (!"pending_payment".equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        // 检查是否已经存在支付记录
        if (exists(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getOrderId, request.getOrderId())
                .eq(Payment::getStatus, "pending"))) {
            throw new BusinessException("订单已存在待支付记录");
        }

        // 创建支付记录
        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderId(request.getOrderId());
        payment.setUserId(userId);
        payment.setAmount(order.getPaymentAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("pending");

        save(payment);

        return convertToResponse(payment, order.getOrderNo());
    }

    @Override
    public PaymentResponse getPayment(Integer userId, Integer paymentId) {
        Payment payment = getOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getId, paymentId)
                .eq(Payment::getUserId, userId));

        if (payment == null) {
            return null;
        }

        Order order = orderService.getOrder(payment.getOrderId());
        return convertToResponse(payment, order.getOrderNo());
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
    public void handlePaymentCallback(String paymentNo, String transactionId, String status, String failureReason) {
        Payment payment = getOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getPaymentNo, paymentNo));

        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }

        // 更新支付记录
        payment.setTransactionId(transactionId);
        payment.setStatus(status);
        payment.setFailureReason(failureReason);
        if ("success".equals(status)) {
            payment.setPaidAt(LocalDateTime.now());
        }
        updateById(payment);

        // 如果支付成功，更新订单状态
        if ("success".equals(status)) {
            orderService.updateOrderStatus(payment.getOrderId(), "paid", payment.getPaidAt());
        }
    }

    private String generatePaymentNo() {
        return "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private PaymentResponse convertToResponse(Payment payment, String orderNo) {
        PaymentResponse response = new PaymentResponse();
        BeanUtils.copyProperties(payment, response);
        response.setOrderNo(orderNo);
        response.setStatusDesc(getStatusDesc(payment.getStatus()));
        return response;
    }

    private Page<PaymentResponse> convertToResponsePage(Page<Payment> page) {
        Page<PaymentResponse> responsePage = new Page<>();
        responsePage.setRecords(page.getRecords().stream()
                .map(payment -> {
                    Order order = orderService.getOrder(payment.getOrderId());
                    return convertToResponse(payment, order.getOrderNo());
                })
                .toList());
        responsePage.setTotal(page.getTotal());
        responsePage.setCurrent(page.getCurrent());
        responsePage.setSize(page.getSize());
        return responsePage;
    }

    private String getStatusDesc(String status) {
        return switch (status) {
            case "pending" -> "待支付";
            case "success" -> "支付成功";
            case "failed" -> "支付失败";
            case "canceled" -> "已取消";
            default -> status;
        };
    }
} 