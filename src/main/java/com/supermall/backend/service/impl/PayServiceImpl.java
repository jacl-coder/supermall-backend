package com.supermall.backend.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermall.backend.common.config.AlipayConfig;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.entity.Order;
import com.supermall.backend.repository.OrderRepository;
import com.supermall.backend.service.PayService;
import com.supermall.backend.vo.PayVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PayServiceImpl implements PayService {

    private final AlipayClient alipayClient;
    private final AlipayConfig alipayConfig;
    private final OrderRepository orderRepository;

    public PayServiceImpl(AlipayClient alipayClient, 
                         AlipayConfig alipayConfig,
                         OrderRepository orderRepository) {
        this.alipayClient = alipayClient;
        this.alipayConfig = alipayConfig;
        this.orderRepository = orderRepository;
    }

    @Override
    public PayVO createAlipayOrder(Long userId, Long orderId) {
        // 查询订单
        Order order = orderRepository.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确");
        }

        try {
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
            request.setReturnUrl(alipayConfig.getReturnUrl());

            // 构建请求参数
            String bizContent = String.format(
                "{" +
                "\"out_trade_no\":\"%s\"," +
                "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                "\"total_amount\":%.2f," +
                "\"subject\":\"订单%s\"," +
                "\"body\":\"订单%s的商品\"" +
                "}",
                order.getOrderNo(),
                order.getPayAmount(),
                order.getOrderNo(),
                order.getOrderNo()
            );
            request.setBizContent(bizContent);

            // 调用支付宝接口
            String form = alipayClient.pageExecute(request).getBody();
            
            return PayVO.builder()
                    .payForm(form)
                    .build();
                    
        } catch (Exception e) {
            log.error("创建支付宝订单失败", e);
            throw new BusinessException("创建支付宝订单失败");
        }
    }

    @Override
    public boolean verifyAlipayCallback(String tradeNo, String outTradeNo, String totalAmount, String sellerId, String appId) {
        // 验证订单信息
        Order order = orderRepository.selectOne(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, outTradeNo)
        );
        
        if (order == null) {
            return false;
        }

        // 验证金额
        if (!order.getPayAmount().toString().equals(totalAmount)) {
            return false;
        }

        // 验证appId
        if (!alipayConfig.getAppId().equals(appId)) {
            return false;
        }

        return true;
    }
} 