package com.supermall.backend.domain.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "商品名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "商品分类不能为空")
    private Long categoryId;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    private BigDecimal price;

    @DecimalMin(value = "0.01", message = "商品原价必须大于0")
    private BigDecimal originalPrice;

    @NotNull(message = "商品库存不能为空")
    @Min(value = 0, message = "商品库存不能小于0")
    private Integer stock;

    private String mainImage;
} 