package com.supermall.backend.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal freightAmount;
    private Integer status;
    private Integer payType;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime receiveTime;
    private LocalDateTime createTime;
    private List<OrderItemVO> items;
    private DeliveryVO delivery;
    
    @Data
    public static class OrderItemVO {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal totalAmount;
    }
    
    @Data
    public static class DeliveryVO {
        private String receiverName;
        private String receiverPhone;
        private String receiverProvince;
        private String receiverCity;
        private String receiverDistrict;
        private String receiverAddress;
        private String deliveryCompany;
        private String deliverySn;
    }
} 