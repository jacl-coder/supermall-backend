package com.supermall.backend.domain.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermall.backend.domain.cart.entity.CartItem;
import com.supermall.backend.domain.cart.service.CartService;
import com.supermall.backend.domain.order.dto.OrderCreateRequest;
import com.supermall.backend.domain.order.dto.OrderResponse;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.entity.OrderItem;
import com.supermall.backend.domain.order.mapper.OrderMapper;
import com.supermall.backend.domain.order.mapper.OrderItemMapper;
import com.supermall.backend.domain.order.service.OrderService;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.service.ProductService;
import com.supermall.backend.domain.order.dto.OrderItemResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.supermall.backend.common.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final CartService cartService;
    private final ProductService productService;
    private final OrderItemMapper orderItemMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(Integer userId, OrderCreateRequest request) {
        // 1. 获取购物车商品
        List<CartItem> cartItems = cartService.getByIds(request.getCartItemIds());

        // 2. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStatus("pending_payment");
        
        // 3. 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (CartItem cartItem : cartItems) {
            Product product = productService.getById(cartItem.getProductId());
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setMerchantId(product.getMerchantId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setProductSnapshot(convertToSnapshot(product));
            
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItems.add(orderItem);
        }
        
        order.setTotalAmount(totalAmount);
        order.setPaymentAmount(totalAmount);
        order.setShippingFee(BigDecimal.ZERO);
        
        // 4. 保存订单
        save(order);
        orderItems.forEach(item -> item.setOrderId(order.getId()));
        orderItemMapper.insertBatch(orderItems);
        
        // 5. 清除购物车
        cartService.deleteByIds(request.getCartItemIds());
        
        return convertToResponse(order, orderItems);
    }

    @Override
    public OrderResponse getOrderDetail(Integer userId, Integer orderId) {
        Order order = getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
        
        if (order == null) {
            return null;
        }

        List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
                
        return convertToResponse(order, orderItems);
    }

    @Override
    public Page<OrderResponse> getUserOrders(Integer userId, String status, int page, int size) {
        Page<Order> orderPage = page(new Page<>(page, size), new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(status != null, Order::getStatus, status)
                .orderByDesc(Order::getCreatedAt));

        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orderPage.getRecords()) {
            List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId()));
            responses.add(convertToResponse(order, orderItems));
        }

        Page<OrderResponse> responsePage = new Page<>();
        responsePage.setRecords(responses);
        responsePage.setTotal(orderPage.getTotal());
        responsePage.setCurrent(orderPage.getCurrent());
        responsePage.setSize(orderPage.getSize());
        
        return responsePage;
    }

    @Override
    @Transactional
    public void cancelOrder(Integer userId, Integer orderId) {
        Order order = getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
                
        if (order != null && "pending_payment".equals(order.getStatus())) {
            order.setStatus("canceled");
            updateById(order);
        } else {
            throw new RuntimeException("订单不存在或状态不允许取消");
        }
    }

    @Override
    @Transactional
    public void confirmReceived(Integer userId, Integer orderId) {
        Order order = getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
                
        if (order != null && "shipped".equals(order.getStatus())) {
            order.setStatus("completed");
            order.setCompletionTime(LocalDateTime.now());
            updateById(order);
        } else {
            throw new RuntimeException("订单不存在或状态不允许确认收货");
        }
    }

    @Override
    public OrderItem getOrderItem(Integer orderItemId) {
        return orderItemMapper.selectById(orderItemId);
    }

    @Override
    public boolean isUserOrder(Integer userId, Integer orderId) {
        Order order = getById(orderId);
        return order != null && order.getUserId().equals(userId);
    }

    @Override
    public Order getOrder(Integer orderId) {
        return getById(orderId);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Integer orderId, String status, LocalDateTime statusTime) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        order.setStatus(status);
        switch (status) {
            case "paid" -> order.setPaymentTime(statusTime);
            case "shipped" -> order.setShippingTime(statusTime);
            case "completed" -> order.setCompletionTime(statusTime);
        }
        updateById(order);
    }

    private String generateOrderNo() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @SneakyThrows
    private String convertToSnapshot(Product product) {
        return objectMapper.writeValueAsString(product);
    }

    @SneakyThrows
    private OrderResponse convertToResponse(Order order, List<OrderItem> items) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setTotalAmount(order.getTotalAmount());
        response.setPaymentAmount(order.getPaymentAmount());
        response.setShippingFee(order.getShippingFee());
        response.setStatus(order.getStatus());
        response.setPaymentTime(order.getPaymentTime());
        response.setShippingTime(order.getShippingTime());
        response.setCompletionTime(order.getCompletionTime());
        response.setAddressSnapshot(order.getAddressSnapshot());
        response.setCreatedAt(order.getCreatedAt());

        List<OrderItemResponse> itemResponses = items.stream()
                .map(item -> {
                    Product product;
                    try {
                        product = objectMapper.readValue(item.getProductSnapshot(), Product.class);
                    } catch (JsonProcessingException e) {
                        throw new BusinessException("订单商品数据解析失败");
                    }
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    itemResponse.setId(item.getId());
                    itemResponse.setProductId(item.getProductId());
                    itemResponse.setMerchantId(item.getMerchantId());
                    itemResponse.setProductName(product.getName());
                    itemResponse.setProductImage(product.getMainImage());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setPrice(item.getPrice());
                    itemResponse.setTotalPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    return itemResponse;
                })
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
    }
} 