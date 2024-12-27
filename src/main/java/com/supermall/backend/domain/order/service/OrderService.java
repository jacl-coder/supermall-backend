package com.supermall.backend.domain.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.order.dto.OrderCreateRequest;
import com.supermall.backend.domain.order.dto.OrderResponse;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.entity.OrderItem;
import java.time.LocalDateTime;

public interface OrderService {
    OrderResponse createOrder(Integer userId, OrderCreateRequest request);
    OrderResponse getOrderDetail(Integer userId, Integer orderId);
    Page<OrderResponse> getUserOrders(Integer userId, String status, int page, int size);
    void cancelOrder(Integer userId, Integer orderId);
    void confirmReceived(Integer userId, Integer orderId);
    
    OrderItem getOrderItem(Integer orderItemId);
    boolean isUserOrder(Integer userId, Integer orderId);
    Order getOrder(Integer orderId);
    void updateOrderStatus(Integer orderId, String status, LocalDateTime statusTime);
} 