package com.supermall.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.dto.OrderCreateDTO;
import com.supermall.backend.dto.OrderDeliveryDTO;
import com.supermall.backend.dto.OrderPayDTO;
import com.supermall.backend.vo.OrderVO;

public interface OrderService {
    OrderVO createOrder(Long userId, OrderCreateDTO orderDTO);
    
    OrderVO getOrderDetail(Long userId, Long orderId);
    Page<OrderVO> getUserOrders(Long userId, Integer status, int pageNum, int pageSize);
    OrderVO payOrder(Long userId, Long orderId, OrderPayDTO payDTO);
    OrderVO deliverOrder(Long orderId, OrderDeliveryDTO deliveryDTO);
    OrderVO confirmReceive(Long userId, Long orderId);
    OrderVO cancelOrder(Long userId, Long orderId);
} 