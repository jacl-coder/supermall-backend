package com.supermall.backend.domain.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.payment.entity.Payment;
import com.supermall.backend.domain.payment.vo.PaymentVO;
import java.util.List;

public interface PaymentService extends IService<Payment> {
    PaymentVO createPayment(PaymentVO paymentVO);
    List<PaymentVO> listUserPayments(Long userId);
    PaymentVO getPaymentDetail(Long id);
    void updatePaymentStatus(Long id, String status);
} 