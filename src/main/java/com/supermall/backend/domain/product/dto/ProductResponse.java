package com.supermall.backend.domain.product.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponse {
    private Long id;
    private Long merchantId;
    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private String mainImage;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 