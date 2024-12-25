package com.supermall.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayVO {
    private String payForm;  // 支付宝支付表单
} 