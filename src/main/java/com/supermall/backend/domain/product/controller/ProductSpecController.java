package com.supermall.backend.domain.product.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.product.dto.ProductSpecRequest;
import com.supermall.backend.domain.product.dto.ProductSpecResponse;
import com.supermall.backend.domain.product.service.ProductSpecService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/specs")
@RequiredArgsConstructor
public class ProductSpecController {

    private final ProductSpecService specService;

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<ProductSpecResponse> addSpec(
            @PathVariable Long productId,
            @Valid @RequestBody ProductSpecRequest request) {
        return Result.success(specService.addSpec(productId, request));
    }

    @DeleteMapping("/{specId}")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Void> deleteSpec(
            @PathVariable Long productId,
            @PathVariable Long specId) {
        specService.deleteSpec(productId, specId);
        return Result.success();
    }

    @PutMapping("/{specId}")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Void> updateSpec(
            @PathVariable Long productId,
            @PathVariable Long specId,
            @Valid @RequestBody ProductSpecRequest request) {
        specService.updateSpec(productId, specId, request);
        return Result.success();
    }

    @GetMapping
    public Result<List<ProductSpecResponse>> getProductSpecs(
            @PathVariable Long productId) {
        return Result.success(specService.getProductSpecs(productId));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Void> batchAddSpecs(
            @PathVariable Long productId,
            @Valid @RequestBody List<ProductSpecRequest> requests) {
        specService.batchAddSpecs(productId, requests);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Void> batchDeleteSpecs(
            @PathVariable Long productId,
            @RequestBody List<Long> specIds) {
        specService.batchDeleteSpecs(productId, specIds);
        return Result.success();
    }
} 