package com.supermall.backend.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductImageRequest {
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;
    
    @NotNull(message = "排序顺序不能为空")
    private Integer sortOrder;
} 