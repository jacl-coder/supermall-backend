package com.supermall.backend.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermall.backend.entity.Order;
import com.supermall.backend.repository.OrderRepository;
import com.supermall.backend.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderScheduleTask {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OrderScheduleTask(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    // 每5分钟执行一次
    @Scheduled(cron = "0 */5 * * * ?")
    public void autoCloseTimeoutOrders() {
        log.info("开始处理超时未支付订单...");
        
        // 查询30分钟内未支付的订单
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(30);
        
        List<Order> orders = orderRepository.selectList(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, 0)  // 未支付状态
                .le(Order::getCreateTime, timeout)
        );

        for (Order order : orders) {
            try {
                orderService.cancelOrder(order.getUserId(), order.getId());
                log.info("自动取消超时订单成功: {}", order.getOrderNo());
            } catch (Exception e) {
                log.error("自动取消超时订单失败: {}", order.getOrderNo(), e);
            }
        }

        log.info("处理超时未支付订单完成，共处理{}个订单", orders.size());
    }

    // 每天凌晨1点执行
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoConfirmTimeoutOrders() {
        log.info("开始处理超时未确认收货订单...");
        
        // 查询15天前已发货的订单
        LocalDateTime timeout = LocalDateTime.now().minusDays(15);
        
        List<Order> orders = orderRepository.selectList(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, 2)  // 已发货状态
                .le(Order::getDeliveryTime, timeout)
        );

        for (Order order : orders) {
            try {
                orderService.confirmReceive(order.getUserId(), order.getId());
                log.info("自动确认收货成功: {}", order.getOrderNo());
            } catch (Exception e) {
                log.error("自动确认收货失败: {}", order.getOrderNo(), e);
            }
        }

        log.info("处理超时未确认收货订单完成，共处理{}个订单", orders.size());
    }
} 