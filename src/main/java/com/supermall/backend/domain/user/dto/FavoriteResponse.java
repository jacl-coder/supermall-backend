package com.supermall.backend.domain.user.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FavoriteResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String description;
    private LocalDateTime createdAt;
} 