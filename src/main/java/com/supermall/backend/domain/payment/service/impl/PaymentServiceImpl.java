package com.supermall.backend.domain.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.domain.notification.service.NotificationService;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.entity.ReturnOrder;
import com.supermall.backend.domain.order.enums.OrderStatus;
import com.supermall.backend.domain.order.service.OrderService;
import com.supermall.backend.domain.payment.dto.PaymentCallback;
import com.supermall.backend.domain.payment.dto.PaymentRequest;
import com.supermall.backend.domain.payment.dto.PaymentResponse;
import com.supermall.backend.domain.payment.entity.Payment;
import com.supermall.backend.domain.payment.exception.PaymentException;
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
            throw new PaymentException(PaymentException.ErrorCode.PAYMENT_NOT_FOUND);
        }
        if (!order.getUserId().equals(userId)) {
            throw new PaymentException(PaymentException.ErrorCode.INVALID_OPERATION, "无权操作此订单");
        }

        // 检查是否已存在支付记录
        Payment existingPayment = getOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getOrderId, request.getOrderId())
                .ne(Payment::getStatus, Payment.Status.FAILED)
                .ne(Payment::getStatus, Payment.Status.CLOSED));
                
        if (existingPayment != null) {
            throw new PaymentException(PaymentException.ErrorCode.PAYMENT_ALREADY_EXISTS);
        }

        // 创建支付记录
        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderId(request.getOrderId());
        payment.setUserId(userId);
        payment.setAmount(order.getPaymentAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(Payment.Status.PENDING);
        payment.setExpireTime(LocalDateTime.now().plusMinutes(30)); // 30分钟超时
        payment.setChannelConfig(generateChannelConfig(request.getPaymentMethod()));
        
        save(payment);
        log.info("创建支付记录成功，支付单号: {}", payment.getPaymentNo());
        
        // 模拟调用支付网关,获取支付链接等信息
        simulatePaymentGateway(payment);
        
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
            throw new PaymentException(PaymentException.ErrorCode.PAYMENT_NOT_FOUND);
        }

        // 验证状态转换
        Payment.Status nextStatus = callback.isSuccess() ? Payment.Status.SUCCESS : Payment.Status.FAILED;
        if (!payment.getStatus().canTransitionTo(nextStatus)) {
            throw new PaymentException(PaymentException.ErrorCode.INVALID_STATUS_TRANSITION);
        }

        payment.setTransactionId(callback.getTransactionId());
        payment.setCallbackContent(callback.toString());
        
        if (callback.isSuccess()) {
            payment.setStatus(Payment.Status.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            
            // 更新订单状态
            orderService.updateOrderStatus(payment.getOrderId(), OrderStatus.PAID, LocalDateTime.now());
            
            // 发送支付成功通知
            sendPaymentNotification(payment, true, null);
        } else {
            payment.setStatus(Payment.Status.FAILED);
            payment.setFailureReason(callback.getFailureReason());
            
            // 发送支付失败通知
            sendPaymentNotification(payment, false, callback.getFailureReason());
        }
        
        updateById(payment);
        log.info("支付回调处理成功，支付单号: {}, 状态: {}", callback.getPaymentNo(), payment.getStatus());
    }

    @Override
    @Transactional
    public void handleRefund(Integer returnId, BigDecimal amount) {
        ReturnOrder returnOrder = orderService.getReturnOrder(returnId);
        if (returnOrder == null) {
            throw new PaymentException(PaymentException.ErrorCode.PAYMENT_NOT_FOUND, "退货单不存在");
        }
        
        // 验证订单状态
        Order order = orderService.getOrder(returnOrder.getOrderId());
        if (!order.getStatus().equals(OrderStatus.COMPLETED)) {
            throw new PaymentException(PaymentException.ErrorCode.ORDER_NOT_COMPLETED);
        }
        
        try {
            // 1. 查找原支付记录
            Payment originalPayment = getOne(new LambdaQueryWrapper<Payment>()
                    .eq(Payment::getOrderId, returnOrder.getOrderId())
                    .eq(Payment::getStatus, Payment.Status.SUCCESS));
                    
            if (originalPayment == null) {
                throw new PaymentException(PaymentException.ErrorCode.PAYMENT_NOT_FOUND, "原支付记录不存在");
            }
            
            // 验证退款金额
            if (amount.compareTo(originalPayment.getAmount()) > 0) {
                throw new PaymentException(PaymentException.ErrorCode.REFUND_AMOUNT_EXCEEDED);
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
            refundPayment.setChannelConfig(originalPayment.getChannelConfig());
            
            save(refundPayment);
            
            // 3. 模拟退款处理
            simulateRefundProcess(refundPayment, originalPayment);
            
            // 4. 更新退款状态
            refundPayment.setStatus(Payment.Status.REFUNDED);
            refundPayment.setPaidAt(LocalDateTime.now());
            updateById(refundPayment);
            
            // 5. 更新订单状态
            if (amount.compareTo(originalPayment.getAmount()) < 0) {
                orderService.updateOrderStatus(order.getId(), OrderStatus.PARTIALLY_REFUNDED, LocalDateTime.now());
            } else {
                orderService.updateOrderStatus(order.getId(), OrderStatus.REFUNDED, LocalDateTime.now());
            }
            
            // 6. 更新订单项退款状态
            orderService.updateOrderItemRefundStatus(returnOrder.getOrderItemId(), true);
            
            // 7. 发送退款成功通知
            sendRefundNotification(refundPayment, true, null);
            
            log.info("退款处理成功，退款单号: {}, 金额: {}", refundPayment.getPaymentNo(), amount);
            
        } catch (Exception e) {
            log.error("退款处理失败", e);
            String errorMessage = e instanceof PaymentException ? e.getMessage() : "退款处理失败";
            sendRefundNotification(null, false, errorMessage);
            throw e;
        }
    }

    @Override
    @Transactional
    public void mockPaymentSuccess(Integer userId, Integer paymentId) {
        Payment payment = validatePaymentOperation(userId, paymentId);
        
        PaymentCallback callback = new PaymentCallback();
        callback.setPaymentNo(payment.getPaymentNo());
        callback.setSuccess(true);
        callback.setTransactionId(UUID.randomUUID().toString());
        
        handlePaymentCallback(callback);
    }

    @Override
    @Transactional
    public void mockPaymentFailure(Integer userId, Integer paymentId, String reason) {
        Payment payment = validatePaymentOperation(userId, paymentId);
        
        PaymentCallback callback = new PaymentCallback();
        callback.setPaymentNo(payment.getPaymentNo());
        callback.setSuccess(false);
        callback.setFailureReason(reason);
        
        handlePaymentCallback(callback);
    }

    @Scheduled(fixedRate = 300000) // 每5分钟检查一次
    @Transactional
    public void checkPaymentTimeout() {
        List<Payment> timeoutPayments = list(new LambdaQueryWrapper<Payment>()
            .eq(Payment::getStatus, Payment.Status.PENDING)
            .lt(Payment::getExpireTime, LocalDateTime.now()));

        for (Payment payment : timeoutPayments) {
            payment.setStatus(Payment.Status.CLOSED);
            payment.setFailureReason("支付超时");
            updateById(payment);
            
            // 更新订单状态为已取消
            orderService.updateOrderStatus(payment.getOrderId(), OrderStatus.CANCELED, LocalDateTime.now());
            
            // 发送支付超时通知
            sendPaymentNotification(payment, false, "支付超时，订单已自动取消");
            
            log.info("支付超时处理，支付单号: {}, 订单号: {}", payment.getPaymentNo(), payment.getOrderId());
        }
    }

    private void sendPaymentNotification(Payment payment, boolean success, String reason) {
        Order order = orderService.getOrder(payment.getOrderId());
        String content = String.format("订单号: %s, 支付金额: %.2f元, %s%s",
            order.getOrderNo(),
            payment.getAmount(),
            success ? "支付成功" : "支付失败",
            reason != null ? "，原因：" + reason : ""
        );
        
        notificationService.sendPaymentStatusNotification(payment.getOrderId(), success, content);
    }

    private void sendRefundNotification(Payment refundPayment, boolean success, String reason) {
        if (refundPayment == null) {
            notificationService.sendRefundStatusNotification(null, false, reason);
            return;
        }
        
        Order order = orderService.getOrder(refundPayment.getOrderId());
        String content = String.format("订单号: %s, 退款金额: %.2f元, %s%s",
            order.getOrderNo(),
            refundPayment.getAmount().abs(),
            success ? "退款成功" : "退款失败",
            reason != null ? "，原因：" + reason : ""
        );
        
        notificationService.sendRefundStatusNotification(refundPayment.getId(), success, content);
    }

    private String generatePaymentNo() {
        return "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private String generateChannelConfig(Payment.PaymentMethod method) {
        // 这里应该根据支付方式��回相应的配置,例如支付网关地址、商户号等
        return String.format("{\"method\": \"%s\", \"gateway\": \"mock_gateway\"}", method);
    }

    private void simulatePaymentGateway(Payment payment) {
        // 模拟调用支付网关
        // 实际项目中这里应该调用真实的支付网关
        log.info("模拟调用支付网关，支付单号: {}", payment.getPaymentNo());
    }

    private void simulateRefundProcess(Payment refundPayment, Payment originalPayment) {
        // 模拟退款处理
        // 实际项目中这里应该调用真实的退款接口
        log.info("模拟退款处理，退款单号: {}, 原支付单号: {}", 
            refundPayment.getPaymentNo(), originalPayment.getPaymentNo());
    }

    private Payment validatePaymentOperation(Integer userId, Integer paymentId) {
        Payment payment = getById(paymentId);
        if (payment == null) {
            throw new PaymentException(PaymentException.ErrorCode.PAYMENT_NOT_FOUND);
        }
        if (!payment.getUserId().equals(userId)) {
            throw new PaymentException(PaymentException.ErrorCode.INVALID_OPERATION, "无权操作此支付记录");
        }
        if (payment.getStatus() != Payment.Status.PENDING) {
            throw new PaymentException(PaymentException.ErrorCode.INVALID_STATUS_TRANSITION);
        }
        return payment;
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
        response.setExpireTime(payment.getExpireTime());
        response.setChannelConfig(payment.getChannelConfig());
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