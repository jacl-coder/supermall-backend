package com.supermall.backend.domain.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.order.dto.OrderCreateRequest;
import com.supermall.backend.domain.order.dto.OrderResponse;
import com.supermall.backend.domain.order.entity.OrderItem;

public interface OrderService {
    OrderResponse createOrder(Long userId, OrderCreateRequest request);
    OrderResponse getOrderDetail(Long userId, Long orderId);
    Page<OrderResponse> getUserOrders(Long userId, String status, int page, int size);
    void cancelOrder(Long userId, Long orderId);
    void confirmReceived(Long userId, Long orderId);
    
    OrderItem getOrderItem(Long orderItemId);
    boolean isUserOrder(Long userId, Long orderId);
} 