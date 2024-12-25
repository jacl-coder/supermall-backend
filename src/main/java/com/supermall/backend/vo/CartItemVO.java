package com.supermall.backend.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemVO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private Integer selected;
    private BigDecimal totalPrice;
} 