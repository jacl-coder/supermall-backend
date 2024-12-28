package com.supermall.backend.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    private Integer parentId;
    
    @NotNull(message = "分类层级不能为空")
    private Integer level;
    
    private Integer sortOrder;
} 