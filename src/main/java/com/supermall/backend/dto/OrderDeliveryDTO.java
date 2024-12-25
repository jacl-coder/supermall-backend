package com.supermall.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OrderDeliveryDTO {
    @NotEmpty(message = "物流公司不能为空")
    private String deliveryCompany;
    
    @NotEmpty(message = "物流单号不能为空")
    private String deliverySn;
} 