package com.supermall.backend.domain.product.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.product.dto.ProductSpecRequest;
import com.supermall.backend.domain.product.dto.ProductSpecResponse;
import com.supermall.backend.domain.product.service.ProductSpecService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品规格管理", description = "包括商品规格的增删改查、规格属性配置等功能")
@RestController
@RequestMapping("/api/products/{productId}/specs")
@RequiredArgsConstructor
public class ProductSpecController {

    private final ProductSpecService specService;

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<ProductSpecResponse> addSpec(
            @PathVariable Integer productId,
            @Valid @RequestBody ProductSpecRequest request) {
        return Result.success(specService.addSpec(productId, request));
    }

    @DeleteMapping("/{specId}")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Void> deleteSpec(
            @PathVariable Integer productId,
            @PathVariable Integer specId) {
        specService.deleteSpec(productId, specId);
        return Result.success();
    }

    @PutMapping("/{specId}")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Void> updateSpec(
            @PathVariable Integer productId,
            @PathVariable Integer specId,
            @Valid @RequestBody ProductSpecRequest request) {
        specService.updateSpec(productId, specId, request);
        return Result.success();
    }

    @GetMapping
    public Result<List<ProductSpecResponse>> getProductSpecs(
            @PathVariable Integer productId) {
        return Result.success(specService.getProductSpecs(productId));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Void> batchAddSpecs(
            @PathVariable Integer productId,
            @Valid @RequestBody List<ProductSpecRequest> requests) {
        specService.batchAddSpecs(productId, requests);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Void> batchDeleteSpecs(
            @PathVariable Integer productId,
            @RequestBody List<Integer> specIds) {
        specService.batchDeleteSpecs(productId, specIds);
        return Result.success();
    }
} 