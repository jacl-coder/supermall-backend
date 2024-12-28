package com.supermall.backend.domain.cart.dto;

import com.supermall.backend.domain.product.entity.Product;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private Boolean selected;
    private BigDecimal totalPrice;
    private Integer stock;
    private Product.Status status;
} 