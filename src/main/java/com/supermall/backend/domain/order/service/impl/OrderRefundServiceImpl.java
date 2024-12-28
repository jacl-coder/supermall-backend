package com.supermall.backend.domain.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.order.dto.RefundRequest;
import com.supermall.backend.domain.order.dto.RefundResponse;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.entity.OrderItem;
import com.supermall.backend.domain.order.entity.OrderRefund;
import com.supermall.backend.domain.order.enums.OrderStatus;
import com.supermall.backend.domain.order.enums.RefundStatus;
import com.supermall.backend.domain.order.mapper.OrderRefundMapper;
import com.supermall.backend.domain.order.service.OrderRefundService;
import com.supermall.backend.domain.order.service.OrderService;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderRefundServiceImpl extends ServiceImpl<OrderRefundMapper, OrderRefund> implements OrderRefundService {

    private final OrderService orderService;
    private final ProductService productService;

    @Override
    @Transactional
    public RefundResponse createRefund(Integer userId, RefundRequest request) {
        // 检查订单项是否存在且属于当前用户
        OrderItem orderItem = orderService.getOrderItem(request.getOrderItemId());
        if (orderItem == null) {
            throw new BusinessException("订单项不存在");
        }
        
        Order order = orderService.getOrder(orderItem.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在或不属于当前用户");
        }
        
        // 检查订单状态是否允许退款
        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.SHIPPED) {
            throw new BusinessException("当前订单状态不允许退款");
        }
        
        // 检查是否已经申请过退款
        if (exists(new LambdaQueryWrapper<OrderRefund>()
                .eq(OrderRefund::getOrderItemId, request.getOrderItemId())
                .ne(OrderRefund::getStatus, RefundStatus.CANCELED))) {
            throw new BusinessException("该订单项已申请退款");
        }
        
        // 检查退款金额是否合法
        if (request.getAmount().compareTo(orderItem.getPaymentAmount()) > 0) {
            throw new BusinessException("退款金额不能大于支付金额");
        }
        
        // 创建退款申请
        OrderRefund refund = new OrderRefund();
        refund.setOrderId(orderItem.getOrderId());
        refund.setOrderItemId(request.getOrderItemId());
        refund.setUserId(userId);
        refund.setMerchantId(orderItem.getMerchantId());
        refund.setAmount(request.getAmount());
        refund.setReason(request.getReason());
        refund.setDescription(request.getDescription());
        refund.setImages(request.getImages() != null ? String.join(",", request.getImages()) : null);
        refund.setStatus(RefundStatus.PENDING);
        
        save(refund);
        
        return convertToResponse(refund);
    }

    @Override
    public RefundResponse getRefundDetail(Integer userId, Integer refundId) {
        OrderRefund refund = getById(refundId);
        if (refund == null || !refund.getUserId().equals(userId)) {
            throw new BusinessException("退款申请不存在或不属于当前用户");
        }
        
        return convertToResponse(refund);
    }

    @Override
    public Page<RefundResponse> getUserRefunds(Integer userId, RefundStatus status, int page, int size) {
        Page<OrderRefund> refundPage = page(new Page<>(page, size),
                new LambdaQueryWrapper<OrderRefund>()
                        .eq(OrderRefund::getUserId, userId)
                        .eq(status != null, OrderRefund::getStatus, status)
                        .orderByDesc(OrderRefund::getCreatedAt));
        
        return convertToResponsePage(refundPage);
    }

    @Override
    @Transactional
    public void cancelRefund(Integer userId, Integer refundId) {
        OrderRefund refund = getById(refundId);
        if (refund == null || !refund.getUserId().equals(userId)) {
            throw new BusinessException("退款申请不存在或不属于当前用户");
        }
        
        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new BusinessException("只能取消待处理的退款申请");
        }
        
        refund.setStatus(RefundStatus.CANCELED);
        updateById(refund);
    }

    @Override
    public Page<RefundResponse> getMerchantRefunds(Integer merchantId, RefundStatus status, int page, int size) {
        Page<OrderRefund> refundPage = page(new Page<>(page, size),
                new LambdaQueryWrapper<OrderRefund>()
                        .eq(OrderRefund::getMerchantId, merchantId)
                        .eq(status != null, OrderRefund::getStatus, status)
                        .orderByDesc(OrderRefund::getCreatedAt));
        
        return convertToResponsePage(refundPage);
    }

    @Override
    @Transactional
    public void approveRefund(Integer merchantId, Integer refundId, String reply) {
        OrderRefund refund = getById(refundId);
        if (refund == null || !refund.getMerchantId().equals(merchantId)) {
            throw new BusinessException("退款申请不存在或不属于当前商家");
        }
        
        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new BusinessException("只能处理待处理的退款申请");
        }
        
        refund.setStatus(RefundStatus.APPROVED);
        refund.setMerchantReply(reply);
        updateById(refund);
        
        // 触发退款流程
        completeRefund(refundId);
    }

    @Override
    @Transactional
    public void rejectRefund(Integer merchantId, Integer refundId, String reply) {
        OrderRefund refund = getById(refundId);
        if (refund == null || !refund.getMerchantId().equals(merchantId)) {
            throw new BusinessException("退款申请不存在或不属于当前商家");
        }
        
        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new BusinessException("只能处理待处理的退款申请");
        }
        
        refund.setStatus(RefundStatus.REJECTED);
        refund.setMerchantReply(reply);
        updateById(refund);
    }

    @Override
    @Transactional
    public void completeRefund(Integer refundId) {
        OrderRefund refund = getById(refundId);
        if (refund == null) {
            throw new BusinessException("退款申请不存在");
        }
        
        if (refund.getStatus() != RefundStatus.APPROVED) {
            throw new BusinessException("只能完成已同意的退款申请");
        }
        
        // 更新订单项状态为已退款
        OrderItem orderItem = orderService.getOrderItem(refund.getOrderItemId());
        if (orderItem == null) {
            throw new BusinessException("订单项不存在");
        }
        
        // 更新订单项退款状态
        orderService.updateOrderItemRefundStatus(orderItem.getId(), true);
        
        // 如果订单下所有商品都已退款，则将订单状态更新为已退款
        Order order = orderService.getOrder(orderItem.getOrderId());
        if (order != null && orderService.isAllItemsRefunded(order.getId())) {
            orderService.updateOrderStatus(order.getId(), OrderStatus.REFUNDED, LocalDateTime.now());
        }
        
        refund.setStatus(RefundStatus.COMPLETED);
        refund.setCompletionTime(LocalDateTime.now());
        updateById(refund);
    }

    private RefundResponse convertToResponse(OrderRefund refund) {
        RefundResponse response = new RefundResponse();
        BeanUtils.copyProperties(refund, response);
        
        // 设置图片列表
        if (refund.getImages() != null) {
            response.setImages(Arrays.asList(refund.getImages().split(",")));
        }
        
        // 设置商品信息
        OrderItem orderItem = orderService.getOrderItem(refund.getOrderItemId());
        if (orderItem != null) {
            Product product = productService.getById(orderItem.getProductId());
            if (product != null) {
                response.setProductName(product.getName());
                response.setProductImage(product.getMainImage());
                response.setProductSpec(orderItem.getProductSpec());
            }
        }
        
        return response;
    }

    private Page<RefundResponse> convertToResponsePage(Page<OrderRefund> page) {
        Page<RefundResponse> responsePage = new Page<>();
        responsePage.setRecords(page.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
        responsePage.setTotal(page.getTotal());
        responsePage.setCurrent(page.getCurrent());
        responsePage.setSize(page.getSize());
        return responsePage;
    }
} 