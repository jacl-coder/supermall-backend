package com.supermall.backend.domain.product.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductImageResponse {
    private Long id;
    private Long productId;
    private String imageUrl;
    private Integer sortOrder;
    private LocalDateTime createdAt;
} 