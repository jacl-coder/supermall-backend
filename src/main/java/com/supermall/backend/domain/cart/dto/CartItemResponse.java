package com.supermall.backend.domain.cart.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private Boolean selected;
    private BigDecimal totalPrice;
} 