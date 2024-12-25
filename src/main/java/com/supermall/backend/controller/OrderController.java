package com.supermall.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermall.backend.common.response.Result;
import com.supermall.backend.dto.OrderCreateDTO;
import com.supermall.backend.service.OrderService;
import com.supermall.backend.service.PayService;
import com.supermall.backend.vo.OrderVO;
import com.supermall.backend.vo.PayVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.supermall.backend.repository.OrderRepository;
import com.supermall.backend.entity.Order;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final PayService payService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, PayService payService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.payService = payService;
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

    @PostMapping("/{orderId}/alipay")
    public Result<PayVO> alipayOrder(@PathVariable Long orderId) {
        Long userId = 1L;
        return Result.success(payService.createAlipayOrder(userId, orderId));
    }

    @PostMapping("/alipay/notify")
    public String alipayNotify(HttpServletRequest request) {
        // 获取支付宝回调参数
        String tradeNo = request.getParameter("trade_no");
        String outTradeNo = request.getParameter("out_trade_no");
        String totalAmount = request.getParameter("total_amount");
        String sellerId = request.getParameter("seller_id");
        String appId = request.getParameter("app_id");

        // 验证回调信息
        if (payService.verifyAlipayCallback(tradeNo, outTradeNo, totalAmount, sellerId, appId)) {
            // 更新订单状态
            Order order = orderRepository.selectOne(
                new LambdaQueryWrapper<Order>()
                    .eq(Order::getOrderNo, outTradeNo)
            );
            
            if (order != null && order.getStatus() == 0) {
                order.setStatus(1);
                order.setPayType(1);
                order.setPayTime(LocalDateTime.now());
                orderRepository.updateById(order);
            }
            
            return "success";
        }
        
        return "fail";
    }
} 