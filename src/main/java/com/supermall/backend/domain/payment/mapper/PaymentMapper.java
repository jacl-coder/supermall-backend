package com.supermall.backend.domain.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.domain.payment.entity.Payment;
import com.supermall.backend.domain.payment.vo.PaymentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
    List<PaymentVO> listByUserId(@Param("userId") Long userId);
    PaymentVO getPaymentById(@Param("id") Long id);
} 