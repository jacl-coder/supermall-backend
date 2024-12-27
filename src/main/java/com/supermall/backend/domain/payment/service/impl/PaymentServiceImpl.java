package com.supermall.backend.domain.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.payment.entity.Payment;
import com.supermall.backend.domain.payment.enums.PaymentMethod;
import com.supermall.backend.domain.payment.enums.PaymentStatus;
import com.supermall.backend.domain.payment.mapper.PaymentMapper;
import com.supermall.backend.domain.payment.service.PaymentService;
import com.supermall.backend.domain.payment.vo.PaymentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentVO createPayment(PaymentVO paymentVO) {
        try {
            // 创建支付记录
            Payment payment = new Payment();
            payment.setOrderNo(paymentVO.getOrderNo());
            payment.setUserId(paymentVO.getUserId());
            payment.setPaymentNo(generatePaymentNo());
            payment.setPaymentMethod(PaymentMethod.valueOf(paymentVO.getPaymentMethod()).getCode());
            payment.setPaymentAmount(paymentVO.getPaymentAmount());
            payment.setStatus(PaymentStatus.PENDING.getCode());
            
            // 保存支付记录
            save(payment);
            
            // 返回支付详情
            return baseMapper.getPaymentById(payment.getId());
        } catch (Exception e) {
            log.error("创建支付记录失败", e);
            throw new ApiException("创建支付记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<PaymentVO> listUserPayments(Long userId) {
        try {
            return baseMapper.listByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户支付记录列表失败", e);
            throw new ApiException("获取用户支付记录列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentVO getPaymentDetail(Long id) {
        try {
            PaymentVO payment = baseMapper.getPaymentById(id);
            if (payment == null) {
                throw new ApiException("支付记录不存在");
            }
            return payment;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取支付记录详情失败", e);
            throw new ApiException("获取支付记录详情失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaymentStatus(Long id, String status) {
        try {
            Payment payment = getById(id);
            if (payment == null) {
                throw new ApiException("支付记录不存在");
            }
            
            payment.setStatus(PaymentStatus.valueOf(status).getCode());
            updateById(payment);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新支付记录状态失败", e);
            throw new ApiException("更新支付记录状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成支付流水号
     */
    private String generatePaymentNo() {
        return UUID.randomUUID().toString().replace("-", "");
    }
} 