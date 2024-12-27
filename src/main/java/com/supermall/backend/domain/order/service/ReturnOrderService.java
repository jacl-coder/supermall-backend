package com.supermall.backend.domain.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.order.entity.ReturnOrder;
import com.supermall.backend.domain.order.dto.ReturnOrderRequest;
import com.supermall.backend.domain.order.dto.ReturnOrderResponse;

public interface ReturnOrderService {
    /**
     * 创建退货订单
     */
    ReturnOrderResponse createReturnOrder(ReturnOrderRequest request, Long userId);
    
    /**
     * 获取退货订单详情
     */
    ReturnOrderResponse getReturnOrder(Long returnId, Long userId);
    
    /**
     * 获取用户的退货订单列表
     */
    Page<ReturnOrderResponse> getUserReturnOrders(Long userId, int page, int size);
    
    /**
     * 获取商家的退货订单列表
     */
    Page<ReturnOrderResponse> getMerchantReturnOrders(Long merchantId, int page, int size);
    
    /**
     * 商家处理退货申请
     */
    ReturnOrderResponse handleReturnOrder(Long returnId, boolean approved, String handlingNotes, Long operatorId);
    
    /**
     * 确认收到退货
     */
    ReturnOrderResponse confirmReturn(Long returnId, Long operatorId);
    
    /**
     * 确认退款完成
     */
    ReturnOrderResponse confirmRefund(Long returnId, Long operatorId);
} 