package com.supermall.backend.domain.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.order.entity.ReturnOrder;
import com.supermall.backend.domain.order.dto.ReturnOrderRequest;
import com.supermall.backend.domain.order.dto.ReturnOrderResponse;

public interface ReturnOrderService {
    /**
     * 创建退货订单
     */
    ReturnOrderResponse createReturnOrder(ReturnOrderRequest request, Integer userId);
    
    /**
     * 获取退货订单详情
     */
    ReturnOrderResponse getReturnOrder(Integer returnId, Integer userId);
    
    /**
     * 获取用户的退货订单列表
     */
    Page<ReturnOrderResponse> getUserReturnOrders(Integer userId, int page, int size);
    
    /**
     * 获取商家的退货订单列表
     */
    Page<ReturnOrderResponse> getMerchantReturnOrders(Integer merchantId, int page, int size);
    
    /**
     * 商家处理退货申请
     */
    ReturnOrderResponse handleReturnOrder(Integer returnId, boolean approved, String handlingNotes, Integer operatorId);
    
    /**
     * 确认收到退货
     */
    ReturnOrderResponse confirmReturn(Integer returnId, Integer operatorId);
    
    /**
     * 确认退款完成
     */
    ReturnOrderResponse confirmRefund(Integer returnId, Integer operatorId);
} 