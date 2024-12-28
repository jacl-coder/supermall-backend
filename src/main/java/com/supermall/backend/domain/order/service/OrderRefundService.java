package com.supermall.backend.domain.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.order.dto.RefundRequest;
import com.supermall.backend.domain.order.dto.RefundResponse;
import com.supermall.backend.domain.order.enums.RefundStatus;

public interface OrderRefundService {
    // 用户相关方法
    RefundResponse createRefund(Integer userId, RefundRequest request);
    RefundResponse getRefundDetail(Integer userId, Integer refundId);
    Page<RefundResponse> getUserRefunds(Integer userId, RefundStatus status, int page, int size);
    void cancelRefund(Integer userId, Integer refundId);
    
    // 商家相关方法
    Page<RefundResponse> getMerchantRefunds(Integer merchantId, RefundStatus status, int page, int size);
    void approveRefund(Integer merchantId, Integer refundId, String reply);
    void rejectRefund(Integer merchantId, Integer refundId, String reply);
    
    // 系统相关方法
    void completeRefund(Integer refundId);
} 