package com.supermall.backend.domain.product.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductSpecResponse {
    private Long id;
    private Long productId;
    private String specName;
    private String specValue;
    private LocalDateTime createdAt;
} 