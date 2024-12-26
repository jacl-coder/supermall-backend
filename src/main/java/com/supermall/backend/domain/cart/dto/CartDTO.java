package com.supermall.backend.domain.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "购物车DTO")
public class CartDTO {
    @Schema(description = "商品ID")
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @Schema(description = "数量")
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;
    
    @Schema(description = "是否选中")
    private Boolean checked = true;
} 