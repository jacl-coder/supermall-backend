package com.supermall.backend.domain.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.vo.OrderVO;

public interface OrderService extends IService<Order> {
    /**
     * 创建订单
     */
    Order createOrder(Order order);
    
    /**
     * 分页获取订单列表
     */
    IPage<OrderVO> listOrders(Integer pageNum, Integer pageSize);
    
    /**
     * 获取订单详情
     */
    OrderVO getOrderDetail(Long id);
    
    /**
     * 更新订单状态
     */
    void updateStatus(Long id, Integer status);
} 