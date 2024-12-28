package com.supermall.backend.domain.cart.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class BatchUpdateQuantityRequest {
    @NotNull(message = "商品ID列表不能为空")
    @NotEmpty(message = "商品ID列表不能为空")
    private List<Integer> itemIds;

    @NotNull(message = "商品数量列表不能为空")
    @NotEmpty(message = "商品数量列表不能为空")
    private List<Integer> quantities;
} 