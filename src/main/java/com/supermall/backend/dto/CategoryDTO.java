package com.supermall.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryDTO {
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    private Long parentId = 0L;
    
    @NotNull(message = "层级不能为空")
    private Integer level;
    
    private Integer sort = 0;
    
    private String icon;
} 