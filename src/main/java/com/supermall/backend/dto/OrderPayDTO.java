package com.supermall.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderPayDTO {
    @NotNull(message = "支付方式不能为空")
    private Integer payType;  // 1->支付宝；2->微信
} 