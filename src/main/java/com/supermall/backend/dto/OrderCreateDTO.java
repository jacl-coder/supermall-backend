package com.supermall.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO {
    @Valid
    @NotNull(message = "收货信息不能为空")
    private DeliveryDTO delivery;
    
    @Valid
    @NotEmpty(message = "订单项不能为空")
    private List<OrderItemDTO> items;
    
    @Data
    public static class DeliveryDTO {
        @NotEmpty(message = "收货人姓名不能为空")
        private String receiverName;
        
        @NotEmpty(message = "收货人电话不能为空")
        private String receiverPhone;
        
        @NotEmpty(message = "省份不能为空")
        private String receiverProvince;
        
        @NotEmpty(message = "城市不能为空")
        private String receiverCity;
        
        @NotEmpty(message = "区县不能为空")
        private String receiverDistrict;
        
        @NotEmpty(message = "详细地址不能为空")
        private String receiverAddress;
    }
    
    @Data
    public static class OrderItemDTO {
        @NotNull(message = "商品ID不能为空")
        private Long productId;
        
        @NotNull(message = "商品数量不能为空")
        private Integer quantity;
    }
} 