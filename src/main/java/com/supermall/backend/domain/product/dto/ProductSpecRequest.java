package com.supermall.backend.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductSpecRequest {
    @NotBlank(message = "规格名称不能为空")
    private String specName;
    
    @NotBlank(message = "规格值不能为空")
    private String specValue;
} 