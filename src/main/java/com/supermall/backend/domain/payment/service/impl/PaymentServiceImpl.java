package com.supermall.backend.domain.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.notification.service.NotificationService;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.entity.ReturnOrder;
import com.supermall.backend.domain.order.enums.OrderStatus;
import com.supermall.backend.domain.order.service.OrderService;
import com.supermall.backend.domain.payment.dto.PaymentCallback;
import com.supermall.backend.domain.payment.dto.PaymentRequest;
import com.supermall.backend.domain.payment.dto.PaymentResponse;
import com.supermall.backend.domain.payment.entity.Payment;
import com.supermall.backend.domain.payment.mapper.PaymentMapper;
import com.supermall.backend.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    private final OrderService orderService;
    private final NotificationService notificationService;

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

        // 检查否已存在支付记录
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
    public void handlePaymentCallback(PaymentCallback callback) {
        Payment payment = getOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getPaymentNo, callback.getPaymentNo()));
                
        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }

        payment.setTransactionId(callback.getTransactionId());
        
        if (callback.isSuccess()) {
            payment.setStatus(Payment.Status.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            
            // 更新订单状态
            orderService.updateOrderStatus(payment.getOrderId(), OrderStatus.PAID, LocalDateTime.now());
            
            // 发送支付成功通知
            notificationService.sendPaymentStatusNotification(
                payment.getOrderId(),
                true,
                String.format("订单支付成功，支付金额：%.2f元", payment.getAmount())
            );
        } else {
            payment.setStatus(Payment.Status.FAILED);
            payment.setFailureReason(callback.getFailureReason());
            
            // 发送支付失败通知
            notificationService.sendPaymentStatusNotification(
                payment.getOrderId(),
                false,
                "支付失败：" + callback.getFailureReason()
            );
        }
        
        updateById(payment);
        log.info("支付回调处理成功，支付单号: {}, 状态: {}", callback.getPaymentNo(), payment.getStatus());
    }

    @Override
    @Transactional
    public void handleRefund(Integer returnId, BigDecimal amount) {
        ReturnOrder returnOrder = orderService.getReturnOrder(returnId);
        if (returnOrder == null) {
            throw new BusinessException("退货单不存在");
        }
        
        try {
            // 处理退款逻辑
            // 1. 查找原支付记录
            Payment originalPayment = getOne(new LambdaQueryWrapper<Payment>()
                    .eq(Payment::getOrderId, returnOrder.getOrderId())
                    .eq(Payment::getStatus, Payment.Status.SUCCESS));
                    
            if (originalPayment == null) {
                throw new BusinessException("原支付记录不存在");
            }
            
            // 2. 创建退款记录
            Payment refundPayment = new Payment();
            refundPayment.setPaymentNo("RF" + generatePaymentNo());
            refundPayment.setOrderId(returnOrder.getOrderId());
            refundPayment.setUserId(returnOrder.getUserId());
            refundPayment.setAmount(amount.negate());  // 使用负数表示退款
            refundPayment.setPaymentMethod(originalPayment.getPaymentMethod());
            refundPayment.setStatus(Payment.Status.REFUND_PENDING);
            refundPayment.setRefundForPaymentId(originalPayment.getId());
            refundPayment.setIsRefund(true);
            
            save(refundPayment);
            
            // 3. 调用实际的退款接口（这里需要集成具体的支付平台）
            // processRefund(refundPayment, originalPayment);
            
            // 4. 更新退款状态（实际项目中应该通过回调更新）
            refundPayment.setStatus(Payment.Status.REFUNDED);
            refundPayment.setPaidAt(LocalDateTime.now());
            updateById(refundPayment);
            
            // 5. 更新订单项退款状态
            orderService.updateOrderItemRefundStatus(returnOrder.getOrderItemId(), true);
            
            // 6. 发送退款成功通知
            notificationService.sendRefundStatusNotification(
                returnId,
                true,
                String.format("退款成功，退款金额：%.2f元", amount)
            );
            
            log.info("退款处理成功，退款单号: {}, 金额: {}", refundPayment.getPaymentNo(), amount);
            
        } catch (Exception e) {
            log.error("退款处理失败", e);
            // 发送退款失败通知
            notificationService.sendRefundStatusNotification(
                returnId,
                false,
                "退款失败：" + e.getMessage()
            );
            throw new BusinessException("退款处理失败：" + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 300000) // 每5分钟检查一次
    @Transactional
    public void checkPaymentTimeout() {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(30);  // 30分钟超时
        
        List<Payment> timeoutPayments = list(new LambdaQueryWrapper<Payment>()
            .eq(Payment::getStatus, Payment.Status.PENDING)
            .lt(Payment::getCreatedAt, timeoutThreshold));

        for (Payment payment : timeoutPayments) {
            payment.setStatus(Payment.Status.CLOSED);
            updateById(payment);
            
            // 更新订单状态为已取消
            orderService.updateOrderStatus(payment.getOrderId(), OrderStatus.CANCELED, LocalDateTime.now());
            
            // 发送支付超时通知
            notificationService.sendPaymentStatusNotification(
                payment.getOrderId(),
                false,
                "支付超时，订单已自动取消"
            );
            
            log.info("支付超时处理，支付单号: {}, 订单号: {}", payment.getPaymentNo(), payment.getOrderId());
        }
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