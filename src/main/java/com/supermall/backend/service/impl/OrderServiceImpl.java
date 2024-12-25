package com.supermall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.dto.OrderCreateDTO;
import com.supermall.backend.dto.OrderPayDTO;
import com.supermall.backend.entity.Order;
import com.supermall.backend.entity.OrderDelivery;
import com.supermall.backend.entity.OrderItem;
import com.supermall.backend.entity.Product;
import com.supermall.backend.repository.OrderDeliveryRepository;
import com.supermall.backend.repository.OrderItemRepository;
import com.supermall.backend.repository.OrderRepository;
import com.supermall.backend.repository.ProductRepository;
import com.supermall.backend.service.OrderService;
import com.supermall.backend.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderDeliveryRepository orderDeliveryRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                          OrderItemRepository orderItemRepository,
                          OrderDeliveryRepository orderDeliveryRepository,
                          ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderDeliveryRepository = orderDeliveryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public OrderVO createOrder(Long userId, OrderCreateDTO orderDTO) {
        // 1. 生成订单号
        String orderNo = generateOrderNo();
        
        // 2. 计算订单金额并创建订单
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (OrderCreateDTO.OrderItemDTO item : orderDTO.getItems()) {
            Product product = productRepository.selectById(item.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在");
            }
            
            // 检查库存
            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException("商品库存不足");
            }
            
            // 计算商品总价
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            
            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalAmount(itemTotal);
            orderItems.add(orderItem);
            
            // 扣减库存
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.updateById(product);
        }
        
        // 3. 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);  // 实际支付金额，可以考虑优惠券等
        order.setFreightAmount(BigDecimal.ZERO);  // 运费，可以根据实际情况计算
        order.setStatus(0);  // 待付款
        order.setPayType(0);  // 未支付
        orderRepository.insert(order);
        
        // 4. 创建订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemRepository.insert(item);
        }
        
        // 5. 创建订单物流信息
        OrderDelivery delivery = new OrderDelivery();
        BeanUtils.copyProperties(orderDTO.getDelivery(), delivery);
        delivery.setOrderId(order.getId());
        orderDeliveryRepository.insert(delivery);
        
        // 6. 构建返回对象
        return buildOrderVO(order, orderItems, delivery);
    }
    
    private String generateOrderNo() {
        // 生成订单号：时间戳 + 6位随机数
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%06d", (int)(Math.random() * 1000000));
    }
    
    private OrderVO buildOrderVO(Order order, List<OrderItem> items, OrderDelivery delivery) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        
        // 设置订单项
        List<OrderVO.OrderItemVO> itemVOs = items.stream().map(item -> {
            OrderVO.OrderItemVO itemVO = new OrderVO.OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItems(itemVOs);
        
        // 设置物流信息
        OrderVO.DeliveryVO deliveryVO = new OrderVO.DeliveryVO();
        BeanUtils.copyProperties(delivery, deliveryVO);
        vo.setDelivery(deliveryVO);
        
        return vo;
    }

    @Override
    public OrderVO getOrderDetail(Long userId, Long orderId) {
        // 查询订单
        Order order = orderRepository.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        // 查询订单项
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemRepository.selectList(itemWrapper);

        // 查询物流信息
        OrderDelivery delivery = orderDeliveryRepository.selectOne(
            new LambdaQueryWrapper<OrderDelivery>()
                .eq(OrderDelivery::getOrderId, orderId)
        );

        return buildOrderVO(order, orderItems, delivery);
    }

    @Override
    public Page<OrderVO> getUserOrders(Long userId, Integer status, int pageNum, int pageSize) {
        // 分页查询订单
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getUserId, userId)
                   .eq(status != null, Order::getStatus, status)
                   .orderByDesc(Order::getCreateTime);
        
        Page<Order> orderPage = orderRepository.selectPage(page, orderWrapper);

        // 转换为VO
        List<OrderVO> orderVOs = orderPage.getRecords().stream()
                .map(order -> {
                    // 查询订单项
                    List<OrderItem> items = orderItemRepository.selectList(
                        new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getOrderId, order.getId())
                    );

                    // 查询物流信息
                    OrderDelivery delivery = orderDeliveryRepository.selectOne(
                        new LambdaQueryWrapper<OrderDelivery>()
                            .eq(OrderDelivery::getOrderId, order.getId())
                    );

                    return buildOrderVO(order, items, delivery);
                })
                .collect(Collectors.toList());

        // 构建返回结果
        Page<OrderVO> resultPage = new Page<>(pageNum, pageSize, orderPage.getTotal());
        resultPage.setRecords(orderVOs);
        return resultPage;
    }

    @Override
    @Transactional
    public OrderVO payOrder(Long userId, Long orderId, OrderPayDTO payDTO) {
        // 查询订单
        Order order = orderRepository.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确");
        }

        // 模拟调用支付接口
        log.info("调用支付接口，订单号：{}，支付方式：{}", order.getOrderNo(), payDTO.getPayType());

        // 更新订单状态
        order.setStatus(1);  // 更新为待发货
        order.setPayType(payDTO.getPayType());
        order.setPayTime(LocalDateTime.now());
        orderRepository.updateById(order);

        // 返回更新后的订单信息
        return getOrderDetail(userId, orderId);
    }
} 