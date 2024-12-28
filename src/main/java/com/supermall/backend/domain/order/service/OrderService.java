package com.supermall.backend.domain.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.order.dto.OrderCreateRequest;
import com.supermall.backend.domain.order.dto.OrderResponse;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.entity.OrderItem;
import com.supermall.backend.domain.order.entity.ReturnOrder;
import com.supermall.backend.domain.order.enums.OrderStatus;
import java.time.LocalDateTime;

public interface OrderService {
    OrderResponse createOrder(Integer userId, OrderCreateRequest request);
    OrderResponse getOrderDetail(Integer userId, Integer orderId);
    Page<OrderResponse> getUserOrders(Integer userId, OrderStatus status, int page, int size);
    void cancelOrder(Integer userId, Integer orderId);
    void confirmReceived(Integer userId, Integer orderId);
    
    OrderItem getOrderItem(Integer orderItemId);
    boolean isUserOrder(Integer userId, Integer orderId);
    Order getOrder(Integer orderId);
    void updateOrderStatus(Integer orderId, OrderStatus status, LocalDateTime statusTime);
    
    // 商家相关方法
    Page<OrderResponse> getMerchantOrders(Integer merchantId, OrderStatus status, int page, int size);
    void shipOrder(Integer merchantId, Integer orderId);
    
    // 退款相关方法
    void updateOrderItemRefundStatus(Integer orderItemId, boolean refunded);
    boolean isAllItemsRefunded(Integer orderId);
    
    /**
     * 获取退货单详情
     */
    ReturnOrder getReturnOrder(Integer returnId);
    
    /**
     * 更新退货单状态
     */
    void updateReturnOrderStatus(Integer returnId, ReturnOrder.ReturnStatus status);
} 