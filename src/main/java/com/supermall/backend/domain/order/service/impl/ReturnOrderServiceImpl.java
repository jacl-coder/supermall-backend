package com.supermall.backend.domain.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.entity.OrderItem;
import com.supermall.backend.domain.order.entity.ReturnOrder;
import com.supermall.backend.domain.order.entity.ReturnOrder.ReturnStatus;
import com.supermall.backend.domain.order.mapper.OrderItemMapper;
import com.supermall.backend.domain.order.mapper.OrderMapper;
import com.supermall.backend.domain.order.mapper.ReturnOrderMapper;
import com.supermall.backend.domain.order.dto.ReturnOrderRequest;
import com.supermall.backend.domain.order.dto.ReturnOrderResponse;
import com.supermall.backend.domain.order.service.ReturnOrderService;
import com.supermall.backend.domain.product.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReturnOrderServiceImpl extends ServiceImpl<ReturnOrderMapper, ReturnOrder> implements ReturnOrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final StockService stockService;

    @Override
    @Transactional
    public ReturnOrderResponse createReturnOrder(ReturnOrderRequest request, Integer userId) {
        // 1. 验证订单和订单项
        OrderItem orderItem = orderItemMapper.selectById(request.getOrderItemId());
        if (orderItem == null) {
            throw new BusinessException("订单项不存在");
        }
        
        Order order = orderMapper.selectById(request.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在或无权限");
        }
        
        // 2. 创建退货订单
        ReturnOrder returnOrder = new ReturnOrder();
        returnOrder.setOrderId(request.getOrderId());
        returnOrder.setOrderItemId(request.getOrderItemId());
        returnOrder.setUserId(userId);
        returnOrder.setMerchantId(orderItem.getMerchantId());
        returnOrder.setReturnAmount(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        returnOrder.setStatus(ReturnStatus.PENDING);
        returnOrder.setReasonType(request.getReasonType());
        returnOrder.setReasonDetail(request.getReasonDetail());
        
        save(returnOrder);
        
        // 3. 转换为响应对象
        ReturnOrderResponse response = new ReturnOrderResponse();
        BeanUtils.copyProperties(returnOrder, response);
        
        return response;
    }

    @Override
    public ReturnOrderResponse getReturnOrder(Integer returnId, Integer userId) {
        ReturnOrder returnOrder = getById(returnId);
        if (returnOrder == null || !returnOrder.getUserId().equals(userId)) {
            throw new BusinessException("退货订单不存在或无权限");
        }
        
        ReturnOrderResponse response = new ReturnOrderResponse();
        BeanUtils.copyProperties(returnOrder, response);
        
        // 获取订单项信息
        OrderItem orderItem = orderItemMapper.selectById(returnOrder.getOrderItemId());
        if (orderItem != null) {
            response.setProductName(orderItem.getProductName());
            response.setProductImage(orderItem.getProductImage());
            response.setQuantity(orderItem.getQuantity());
            response.setPrice(orderItem.getPrice());
        }
        
        return response;
    }

    @Override
    public Page<ReturnOrderResponse> getUserReturnOrders(Integer userId, int page, int size) {
        Page<ReturnOrder> returnOrderPage = page(
            new Page<>(page, size),
            new LambdaQueryWrapper<ReturnOrder>()
                .eq(ReturnOrder::getUserId, userId)
                .orderByDesc(ReturnOrder::getCreatedAt)
        );
        
        return convertToResponsePage(returnOrderPage);
    }

    @Override
    public Page<ReturnOrderResponse> getMerchantReturnOrders(Integer merchantId, int page, int size) {
        Page<ReturnOrder> returnOrderPage = page(
            new Page<>(page, size),
            new LambdaQueryWrapper<ReturnOrder>()
                .eq(ReturnOrder::getMerchantId, merchantId)
                .orderByDesc(ReturnOrder::getCreatedAt)
        );
        
        return convertToResponsePage(returnOrderPage);
    }

    @Override
    @Transactional
    public ReturnOrderResponse handleReturnOrder(Integer returnId, boolean approved, String handlingNotes, Integer operatorId) {
        ReturnOrder returnOrder = getById(returnId);
        if (returnOrder == null) {
            throw new BusinessException("退货订单不存在");
        }
        
        if (returnOrder.getStatus() != ReturnStatus.PENDING) {
            throw new BusinessException("退货订单状态不正确");
        }
        
        returnOrder.setStatus(approved ? ReturnStatus.APPROVED : ReturnStatus.REJECTED);
        returnOrder.setHandlingNotes(handlingNotes);
        updateById(returnOrder);
        
        ReturnOrderResponse response = new ReturnOrderResponse();
        BeanUtils.copyProperties(returnOrder, response);
        
        return response;
    }

    @Override
    @Transactional
    public ReturnOrderResponse confirmReturn(Integer returnId, Integer operatorId) {
        ReturnOrder returnOrder = getById(returnId);
        if (returnOrder == null) {
            throw new BusinessException("退货订单不存在");
        }
        
        if (returnOrder.getStatus() != ReturnStatus.APPROVED) {
            throw new BusinessException("退货订单状态不正确");
        }
        
        // 更新退货单状态
        returnOrder.setStatus(ReturnStatus.RETURNED);
        updateById(returnOrder);
        
        // 恢复库存
        OrderItem orderItem = orderItemMapper.selectById(returnOrder.getOrderItemId());
        if (orderItem != null) {
            stockService.releaseStock(
                orderItem.getProductId(),
                orderItem.getQuantity(),
                returnOrder.getId(),
                operatorId
            );
        }
        
        ReturnOrderResponse response = new ReturnOrderResponse();
        BeanUtils.copyProperties(returnOrder, response);
        
        return response;
    }

    @Override
    @Transactional
    public ReturnOrderResponse confirmRefund(Integer returnId, Integer operatorId) {
        ReturnOrder returnOrder = getById(returnId);
        if (returnOrder == null) {
            throw new BusinessException("退货订单不存在");
        }
        
        if (returnOrder.getStatus() != ReturnStatus.RETURNED) {
            throw new BusinessException("退货订单状态不正确");
        }
        
        returnOrder.setStatus(ReturnStatus.REFUNDED);
        updateById(returnOrder);
        
        ReturnOrderResponse response = new ReturnOrderResponse();
        BeanUtils.copyProperties(returnOrder, response);
        
        return response;
    }
    
    private Page<ReturnOrderResponse> convertToResponsePage(Page<ReturnOrder> returnOrderPage) {
        Page<ReturnOrderResponse> responsePage = new Page<>(
            returnOrderPage.getCurrent(),
            returnOrderPage.getSize(),
            returnOrderPage.getTotal()
        );
        
        responsePage.setRecords(returnOrderPage.getRecords().stream().map(returnOrder -> {
            ReturnOrderResponse response = new ReturnOrderResponse();
            BeanUtils.copyProperties(returnOrder, response);
            
            // 获取订单项信息
            OrderItem orderItem = orderItemMapper.selectById(returnOrder.getOrderItemId());
            if (orderItem != null) {
                response.setProductName(orderItem.getProductName());
                response.setProductImage(orderItem.getProductImage());
                response.setQuantity(orderItem.getQuantity());
                response.setPrice(orderItem.getPrice());
            }
            
            return response;
        }).toList());
        
        return responsePage;
    }
} 