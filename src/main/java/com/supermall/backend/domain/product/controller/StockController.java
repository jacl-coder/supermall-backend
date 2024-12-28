package com.supermall.backend.domain.product.controller;

import com.supermall.backend.common.security.aspect.RequirePermission;
import com.supermall.backend.domain.product.entity.StockMovement;
import com.supermall.backend.domain.product.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "库存管理", description = "包括库存查询、更新等功能")
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Data
    public static class StockAdjustRequest {
        @NotNull(message = "商品ID不能为空")
        private Integer productId;
        
        @NotNull(message = "调整数量不能为空")
        private Integer quantity;
        
        private String notes;
    }

    @Operation(summary = "检查库存")
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkStock(
            @RequestParam @NotNull Integer productId,
            @RequestParam @Min(1) Integer quantity) {
        return ResponseEntity.ok(stockService.checkStock(productId, quantity));
    }

    @Operation(summary = "手动调整库存")
    @RequirePermission("product:manage")
    @PostMapping("/adjust")
    public ResponseEntity<Boolean> adjustStock(
            @RequestBody @Valid StockAdjustRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(stockService.adjustStock(
                request.getProductId(),
                request.getQuantity(),
                Integer.valueOf(userDetails.getUsername()),
                request.getNotes()
        ));
    }
} 