package com.supermall.backend.domain.product.dto;

import com.supermall.backend.domain.product.entity.Product;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "商品名称不能为空")
    @Size(min = 2, max = 50, message = "商品名称长度必须在2-50个字符之间")
    private String name;

    @NotBlank(message = "商品描述不能为空")
    @Size(max = 500, message = "商品描述不能超过500个字符")
    private String description;

    @NotNull(message = "商品分类不能为空")
    private Integer categoryId;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @DecimalMax(value = "99999999.99", message = "商品价格超出范围")
    private BigDecimal price;

    @NotNull(message = "商品原价不能为空")
    @DecimalMin(value = "0.01", message = "商品原价必须大于0")
    @DecimalMax(value = "99999999.99", message = "商品原价超出范围")
    private BigDecimal originalPrice;

    @NotNull(message = "商品库存不能为空")
    @Min(value = 0, message = "商品库存不能小于0")
    @Max(value = 999999, message = "商品库存不能超过999999")
    private Integer stock;

    @NotBlank(message = "商品主图不能为空")
    @Pattern(regexp = "^https?://[^\\s/$.?#].[^\\s]*$", message = "商品主图URL格式不正确")
    private String mainImage;

    private Product.Status status;
} 