package com.supermall.backend.service;

import com.supermall.backend.vo.PayVO;

public interface PayService {
    PayVO createAlipayOrder(Long userId, Long orderId);
    boolean verifyAlipayCallback(String tradeNo, String outTradeNo, String totalAmount, String sellerId, String appId);
} 