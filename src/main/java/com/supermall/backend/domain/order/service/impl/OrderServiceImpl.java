package com.supermall.backend.domain.order.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.entity.OrderItem;
import com.supermall.backend.domain.order.mapper.OrderItemMapper;
import com.supermall.backend.domain.order.mapper.OrderMapper;
import com.supermall.backend.domain.order.service.OrderService;
import com.supermall.backend.domain.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    private final OrderItemMapper orderItemMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Order order) {
        log.debug("创建订单: {}", order);
        try {
            // 参数校验
            if (order.getUserId() == null) {
                throw new ApiException("用户ID不能为空");
            }
            if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
                throw new ApiException("订单项不能为空");
            }
            
            // 生成订单编号
            String orderNo = generateOrderNo();
            order.setOrderNo(orderNo);
            
            // 设置初始状态
            if (order.getStatus() == null) {
                order.setStatus(0);  // 默认待付款
            }
            
            // 计算运费（可以根据实际业务逻辑修改）
            if (order.getFreightAmount() == null) {
                order.setFreightAmount(BigDecimal.ZERO);
            }
            
            // 保存订单
            boolean saved = save(order);
            if (!saved) {
                throw new ApiException("保存订单失败");
            }
            
            // 保存订单项
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (OrderItem item : order.getOrderItems()) {
                if (item.getProductId() == null) {
                    throw new ApiException("商品ID不能为空");
                }
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    throw new ApiException("商品数量必须大于0");
                }
                if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ApiException("商品价格必须大于0");
                }
                
                // 设置订单项关联信息
                item.setOrderId(order.getId());
                item.setOrderNo(orderNo);
                
                // 计算订单项总金额
                item.setTotalAmount(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                totalAmount = totalAmount.add(item.getTotalAmount());
                
                boolean itemSaved = orderItemMapper.insert(item) > 0;
                if (!itemSaved) {
                    throw new ApiException("保存订单项失败");
                }
            }
            
            // 更新订单总金额
            order.setTotalAmount(totalAmount);
            order.setPayAmount(totalAmount.add(order.getFreightAmount()));
            updateById(order);
            
            return order;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建订单失败", e);
            throw new ApiException("创建订单失败: " + e.getMessage());
        }
    }
    
    @Override
    public IPage<OrderVO> listOrders(Integer pageNum, Integer pageSize) {
        log.debug("获取订单列表: pageNum={}, pageSize={}", pageNum, pageSize);
        try {
            Page<OrderVO> page = new Page<>(pageNum, pageSize);
            return baseMapper.listOrders(page);
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            throw new ApiException("获取订单列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public OrderVO getOrderDetail(Long id) {
        log.debug("获取订单详情: id={}", id);
        try {
            OrderVO order = baseMapper.getOrderById(id);
            if (order == null) {
                throw new ApiException("订单不存在");
            }
            return order;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            throw new ApiException("获取订单详情失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        log.debug("更新订单状态: id={}, status={}", id, status);
        try {
            Order order = getById(id);
            if (order == null) {
                throw new ApiException("订单不存在");
            }
            
            order.setStatus(status);
            updateById(order);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新订单状态失败", e);
            throw new ApiException("更新订单状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        return "O" + LocalDateTime.now().toString().replace("-", "")
            .replace(":", "")
            .replace(".", "")
            + String.format("%04d", (int)(Math.random() * 10000));
    }
} 