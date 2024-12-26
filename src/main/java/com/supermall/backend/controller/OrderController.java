package com.supermall.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermall.backend.common.response.Result;
import com.supermall.backend.dto.OrderCreateDTO;
import com.supermall.backend.dto.OrderDeliveryDTO;
import com.supermall.backend.dto.OrderPayDTO;
import com.supermall.backend.service.OrderService;
import com.supermall.backend.vo.OrderVO;
import com.supermall.backend.repository.OrderRepository;
import com.supermall.backend.entity.Order;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public Result<OrderVO> createOrder(@Valid @RequestBody OrderCreateDTO orderDTO) {
        Long userId = 1L;
        return Result.success(orderService.createOrder(userId, orderDTO));
    }

    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        Long userId = 1L;
        return Result.success(orderService.getOrderDetail(userId, orderId));
    }

    @GetMapping
    public Result<Page<OrderVO>> getUserOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = 1L;
        return Result.success(orderService.getUserOrders(userId, status, pageNum, pageSize));
    }

    @PostMapping("/{orderId}/pay")
    public Result<OrderVO> payOrder(@PathVariable Long orderId) {
        Long userId = 1L; // TODO: 从token中获取
        OrderPayDTO payDTO = new OrderPayDTO();
        payDTO.setPayType(1); // 默认使用支付宝支付类型
        return Result.success(orderService.payOrder(userId, orderId, payDTO));
    }

    @PostMapping("/{orderId}/deliver")
    public Result<OrderVO> deliverOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderDeliveryDTO deliveryDTO) {
        return Result.success(orderService.deliverOrder(orderId, deliveryDTO));
    }

    @PostMapping("/{orderId}/receive")
    public Result<OrderVO> confirmReceive(@PathVariable Long orderId) {
        Long userId = 1L; // TODO: 从token中获取
        return Result.success(orderService.confirmReceive(userId, orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public Result<OrderVO> cancelOrder(@PathVariable Long orderId) {
        Long userId = 1L; // TODO: 从token中获取
        return Result.success(orderService.cancelOrder(userId, orderId));
    }
} 