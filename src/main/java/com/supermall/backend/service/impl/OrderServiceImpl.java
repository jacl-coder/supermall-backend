package com.supermall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.dto.OrderCreateDTO;
import com.supermall.backend.dto.OrderDeliveryDTO;
import com.supermall.backend.dto.OrderPayDTO;
import com.supermall.backend.entity.Order;
import com.supermall.backend.entity.OrderDelivery;
import com.supermall.backend.entity.OrderItem;
import com.supermall.backend.entity.Product;
import com.supermall.backend.entity.OrderLog;
import com.supermall.backend.repository.OrderDeliveryRepository;
import com.supermall.backend.repository.OrderItemRepository;
import com.supermall.backend.repository.OrderRepository;
import com.supermall.backend.repository.ProductRepository;
import com.supermall.backend.repository.OrderLogRepository;
import com.supermall.backend.service.OrderService;
import com.supermall.backend.service.NotificationService;
import com.supermall.backend.service.ProductSalesService;
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
    private final OrderLogRepository orderLogRepository;
    private final NotificationService notificationService;
    private final ProductSalesService productSalesService;

    public OrderServiceImpl(OrderRepository orderRepository,
                          OrderItemRepository orderItemRepository,
                          OrderDeliveryRepository orderDeliveryRepository,
                          ProductRepository productRepository,
                          OrderLogRepository orderLogRepository,
                          NotificationService notificationService,
                          ProductSalesService productSalesService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderDeliveryRepository = orderDeliveryRepository;
        this.productRepository = productRepository;
        this.orderLogRepository = orderLogRepository;
        this.notificationService = notificationService;
        this.productSalesService = productSalesService;
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
            String statusDesc = getOrderStatusDesc(order.getStatus());
            throw new BusinessException(String.format("当前订单状态为%s，不能支付", statusDesc));
        }

        // 直接更新订单状态为已支付
        order.setStatus(1);  // 更新为待发货
        order.setPayType(payDTO.getPayType());
        order.setPayTime(LocalDateTime.now());
        orderRepository.updateById(order);

        // 返回更新后的订单信息
        return getOrderDetail(userId, orderId);
    }

    @Override
    @Transactional
    public OrderVO deliverOrder(Long orderId, OrderDeliveryDTO deliveryDTO) {
        // 查询订单
        Order order = orderRepository.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (order.getStatus() != 1) {  // 1: 待发货
            String statusDesc = getOrderStatusDesc(order.getStatus());
            throw new BusinessException(String.format("当前订单状态为%s，不能发货", statusDesc));
        }

        // 查询订单物流信息
        OrderDelivery delivery = orderDeliveryRepository.selectOne(
            new LambdaQueryWrapper<OrderDelivery>()
                .eq(OrderDelivery::getOrderId, orderId)
        );
        
        if (delivery == null) {
            throw new BusinessException("订单物流信息不存在");
        }

        // 更新物流信息
        delivery.setDeliveryCompany(deliveryDTO.getDeliveryCompany());
        delivery.setDeliverySn(deliveryDTO.getDeliverySn());
        orderDeliveryRepository.updateById(delivery);

        // 更新订单状态
        order.setStatus(2);  // 2: 已发货
        order.setDeliveryTime(LocalDateTime.now());
        orderRepository.updateById(order);

        // 查询订单项
        List<OrderItem> orderItems = orderItemRepository.selectList(
            new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)
        );

        // 发送发货通知给买家
        notificationService.sendOrderDeliveryNotification(
            order.getUserId(), 
            order.getOrderNo(), 
            delivery.getDeliveryCompany(), 
            delivery.getDeliverySn()
        );

        return buildOrderVO(order, orderItems, delivery);
    }

    @Override
    @Transactional
    public OrderVO confirmReceive(Long userId, Long orderId) {
        // 查询订单
        Order order = orderRepository.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (order.getStatus() != 2) {  // 2: 已发货
            String statusDesc = getOrderStatusDesc(order.getStatus());
            throw new BusinessException(String.format("当前订单状态为%s，不能确认收货", statusDesc));
        }

        // 更新订单状态
        order.setStatus(3);  // 3: 已完成
        order.setReceiveTime(LocalDateTime.now());
        orderRepository.updateById(order);

        // 查询订单项和物流信息
        List<OrderItem> orderItems = orderItemRepository.selectList(
            new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)
        );

        OrderDelivery delivery = orderDeliveryRepository.selectOne(
            new LambdaQueryWrapper<OrderDelivery>()
                .eq(OrderDelivery::getOrderId, orderId)
        );

        // 处理订单完成后的业务逻辑
        // 1. 更新商品销量
        for (OrderItem item : orderItems) {
            productSalesService.increaseSales(item.getProductId(), item.getQuantity());
        }

        // 2. 发送订单完成通知
        notificationService.sendOrderCompletedNotification(userId, order.getOrderNo());

        // 3. 记录订单日志
        saveOrderLog(order, "确认收货",
            order.getReceiveTime() == null ? "用户确认" : "系统自动确认");

        return buildOrderVO(order, orderItems, delivery);
    }

    @Override
    @Transactional
    public OrderVO cancelOrder(Long userId, Long orderId) {
        // 查询订单
        Order order = orderRepository.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (order.getStatus() != 0) {  // 只有未支付的订单可以取消
            String statusDesc = getOrderStatusDesc(order.getStatus());
            throw new BusinessException(String.format("当前订单状态为%s，不能取消", statusDesc));
        }

        // 查询订单项
        List<OrderItem> orderItems = orderItemRepository.selectList(
            new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)
        );

        // 恢复商品库存
        for (OrderItem item : orderItems) {
            Product product = productRepository.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.updateById(product);
            }
        }

        // 更新订单状态
        order.setStatus(4);  // 4: 已取消
        order.setCancelTime(LocalDateTime.now());
        orderRepository.updateById(order);

        // 查询物流信息
        OrderDelivery delivery = orderDeliveryRepository.selectOne(
            new LambdaQueryWrapper<OrderDelivery>()
                .eq(OrderDelivery::getOrderId, orderId)
        );

        // 发送订单取消通知
        notificationService.sendOrderCancelledNotification(userId, order.getOrderNo());

        // 记录订单日志
        saveOrderLog(order, "取消订单", 
            order.getCancelTime() == null ? "用户取消" : "系统自动取消");

        return buildOrderVO(order, orderItems, delivery);
    }

    private void saveOrderLog(Order order, String action, String note) {
        OrderLog log = OrderLog.builder()
            .orderId(order.getId())
            .userId(order.getUserId())
            .orderStatus(order.getStatus())
            .action(action)
            .note(note)
            .createTime(LocalDateTime.now())
            .build();
        
        orderLogRepository.insert(log);
    }

    /**
     * 获取订单状态描述
     */
    private String getOrderStatusDesc(Integer status) {
        switch (status) {
            case 0:
                return "待付款";
            case 1:
                return "待发货";
            case 2:
                return "已发货";
            case 3:
                return "已完成";
            case 4:
                return "已取消";
            case 5:
                return "无效订单";
            default:
                return "未知状态";
        }
    }
} 