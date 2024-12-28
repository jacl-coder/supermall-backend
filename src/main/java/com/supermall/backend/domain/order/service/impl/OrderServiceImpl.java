package com.supermall.backend.domain.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.cart.entity.CartItem;
import com.supermall.backend.domain.cart.service.CartService;
import com.supermall.backend.domain.order.dto.OrderCreateRequest;
import com.supermall.backend.domain.order.dto.OrderResponse;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.entity.OrderItem;
import com.supermall.backend.domain.order.enums.OrderStatus;
import com.supermall.backend.domain.order.mapper.OrderMapper;
import com.supermall.backend.domain.order.mapper.OrderItemMapper;
import com.supermall.backend.domain.order.service.OrderService;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.service.ProductService;
import com.supermall.backend.domain.order.dto.OrderItemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("开始创建订单，用户ID: {}, 请求参数: {}", userId, request);
        
        // 1. 获取购物车商品
        List<CartItem> cartItems = cartService.getByIds(request.getCartItemIds());
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BusinessException("购物车项不存在");
        }
        
        // 2. 验证购物车项是否属于当前用户
        boolean isValidCart = cartItems.stream()
                .allMatch(item -> item.getUserId().equals(userId));
        if (!isValidCart) {
            throw new BusinessException("无效的购物车项");
        }

        // 3. 获取商品信息并验证库存
        List<Integer> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .collect(Collectors.toList());
        List<Product> products = productService.listByIds(productIds);
        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 4. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        
        // 5. 创建订单项和计算金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (CartItem cartItem : cartItems) {
            Product product = productMap.get(cartItem.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在: " + cartItem.getProductId());
            }
            
            // 检查商品状态
            if (product.getStatus() != Product.Status.ON_SALE) {
                throw new BusinessException("商品已下架: " + product.getName());
            }
            
            // 检查库存
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException("商品库存不足: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setMerchantId(product.getMerchantId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            try {
                orderItem.setProductSnapshot(objectMapper.writeValueAsString(product));
            } catch (JsonProcessingException e) {
                throw new BusinessException("商品数据序列化失败");
            }
            
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItems.add(orderItem);
            
            // 扣减库存
            product.setStock(product.getStock() - cartItem.getQuantity());
            productService.updateById(product);
        }
        
        order.setTotalAmount(totalAmount);
        order.setPaymentAmount(totalAmount);
        order.setShippingFee(BigDecimal.ZERO);
        
        // 6. 保存订单
        save(order);
        orderItems.forEach(item -> item.setOrderId(order.getId()));
        orderItemMapper.insertBatch(orderItems);
        
        // 7. 清除购物车
        cartService.deleteByIds(request.getCartItemIds());
        
        log.info("订单创建成功，订单号: {}", order.getOrderNo());
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
    public Page<OrderResponse> getUserOrders(Integer userId, OrderStatus status, int page, int size) {
        // 1. 查询订单
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(status != null, Order::getStatus, status)
                .orderByDesc(Order::getCreatedAt);
                
        Page<Order> orderPage = page(new Page<>(page, size), wrapper);
        return convertToResponsePage(orderPage);
    }

    @Override
    @Transactional
    public void cancelOrder(Integer userId, Integer orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("只能取消待付款订单");
        }

        order.setStatus(OrderStatus.CANCELED);
        updateById(order);
    }

    @Override
    @Transactional
    public void confirmReceived(Integer userId, Integer orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new BusinessException("只能确认收货已发货订单");
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletionTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    public OrderItem getOrderItem(Integer orderItemId) {
        return orderItemMapper.selectById(orderItemId);
    }

    @Override
    public boolean isUserOrder(Integer userId, Integer orderId) {
        return exists(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
    }

    @Override
    public Order getOrder(Integer orderId) {
        return getById(orderId);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Integer orderId, OrderStatus status, LocalDateTime statusTime) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 验证状态流转的合法性
        validateStatusTransition(order.getStatus(), status);

        // 更新状态和相应的时间
        order.setStatus(status);
        switch (status) {
            case PAID:
                order.setPaymentTime(statusTime);
                break;
            case SHIPPED:
                order.setShippingTime(statusTime);
                break;
            case COMPLETED:
                order.setCompletionTime(statusTime);
                break;
            default:
                break;
        }

        updateById(order);
        log.info("订单状态更新成功，订单号: {}, 新状态: {}", order.getOrderNo(), status);
    }

    @Override
    public Page<OrderResponse> getMerchantOrders(Integer merchantId, OrderStatus status, int page, int size) {
        // 1. 查询订单项
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getMerchantId, merchantId)
                .select(OrderItem::getOrderId)
                .groupBy(OrderItem::getOrderId);
                
        List<OrderItem> orderItems = orderItemMapper.selectList(itemWrapper);
        if (orderItems.isEmpty()) {
            return new Page<OrderResponse>().setRecords(Collections.emptyList());
        }
        
        List<Integer> orderIds = orderItems.stream()
                .map(OrderItem::getOrderId)
                .collect(Collectors.toList());

        // 2. 查询订单
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<Order>()
                .in(Order::getId, orderIds)
                .eq(status != null, Order::getStatus, status)
                .orderByDesc(Order::getCreatedAt);
                
        Page<Order> orderPage = page(new Page<>(page, size), orderWrapper);
        return convertToResponsePage(orderPage);
    }

    @Override
    @Transactional
    public void shipOrder(Integer merchantId, Integer orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 验证订单中是否包含该商家的商品
        boolean hasMerchantItems = orderItemMapper.exists(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)
                .eq(OrderItem::getMerchantId, merchantId));
        if (!hasMerchantItems) {
            throw new BusinessException("无权操作此订单");
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException("只能发货已付款订单");
        }

        updateOrderStatus(orderId, OrderStatus.SHIPPED, LocalDateTime.now());
        log.info("订单发货成功，订单号: {}", order.getOrderNo());
    }

    /**
     * 验证订单状态流转的合法性
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            return;
        }

        switch (currentStatus) {
            case PENDING_PAYMENT:
                if (newStatus != OrderStatus.PAID && newStatus != OrderStatus.CANCELED) {
                    throw new BusinessException("待付款订单只能变更为已付款或已取消状态");
                }
                break;
            case PAID:
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELED) {
                    throw new BusinessException("已付款订单只能变更为已发货或已取消状态");
                }
                break;
            case SHIPPED:
                if (newStatus != OrderStatus.COMPLETED) {
                    throw new BusinessException("已发货订单只能变更为已完成状态");
                }
                break;
            case COMPLETED:
            case CANCELED:
                throw new BusinessException("当前订单状态不允许变更");
            default:
                throw new BusinessException("未知的订单状态");
        }
    }

    private String generateOrderNo() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

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

    private Page<OrderResponse> convertToResponsePage(Page<Order> page) {
        if (page.getRecords().isEmpty()) {
            return new Page<OrderResponse>().setRecords(Collections.emptyList());
        }

        // 1. 批量查询订单项
        List<Integer> orderIds = page.getRecords().stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        List<OrderItem> allOrderItems = orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItem>()
                .in(OrderItem::getOrderId, orderIds));
                
        // 2. 按订单ID分组
        Map<Integer, List<OrderItem>> orderItemMap = allOrderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        // 3. 转换响应对象
        List<OrderResponse> responses = page.getRecords().stream()
                .map(order -> convertToResponse(order, orderItemMap.getOrDefault(order.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        // 4. 设置分页信息
        Page<OrderResponse> responsePage = new Page<>();
        responsePage.setRecords(responses);
        responsePage.setTotal(page.getTotal());
        responsePage.setCurrent(page.getCurrent());
        responsePage.setSize(page.getSize());
        
        return responsePage;
    }

    @Override
    @Transactional
    public void updateOrderItemRefundStatus(Integer orderItemId, boolean refunded) {
        OrderItem orderItem = getOrderItem(orderItemId);
        if (orderItem == null) {
            throw new BusinessException("订单项不存在");
        }
        
        orderItem.setIsRefunded(refunded);
        orderItemMapper.updateById(orderItem);
    }

    @Override
    public boolean isAllItemsRefunded(Integer orderId) {
        List<OrderItem> items = orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)
        );
        
        if (items.isEmpty()) {
            return false;
        }
        
        // 检查是否所有订单项都已退款
        return items.stream().allMatch(OrderItem::getIsRefunded);
    }
} 