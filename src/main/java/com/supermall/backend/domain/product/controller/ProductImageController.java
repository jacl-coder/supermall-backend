package com.supermall.backend.domain.product.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.product.dto.ProductImageRequest;
import com.supermall.backend.domain.product.dto.ProductImageResponse;
import com.supermall.backend.domain.product.service.ProductImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品图片管理", description = "提供商品图片相关的API接口，包括图片上传、删除、查询等功能，支持商品主图和详情图的管理，确保商品展示的完整性")
@RestController
@RequestMapping("/api/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService imageService;

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<ProductImageResponse> addImage(
            @PathVariable Integer productId,
            @Valid @RequestBody ProductImageRequest request) {
        return Result.success(imageService.addImage(productId, request));
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Void> deleteImage(
            @PathVariable Integer productId,
            @PathVariable Integer imageId) {
        imageService.deleteImage(productId, imageId);
        return Result.success();
    }

    @PutMapping("/{imageId}/sort")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Void> updateImageSort(
            @PathVariable Integer productId,
            @PathVariable Integer imageId,
            @RequestParam Integer sortOrder) {
        imageService.updateImageSort(productId, imageId, sortOrder);
        return Result.success();
    }

    @GetMapping
    public Result<List<ProductImageResponse>> getProductImages(
            @PathVariable Integer productId) {
        return Result.success(imageService.getProductImages(productId));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Void> batchAddImages(
            @PathVariable Integer productId,
            @Valid @RequestBody List<ProductImageRequest> requests) {
        imageService.batchAddImages(productId, requests);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Void> batchDeleteImages(
            @PathVariable Integer productId,
            @RequestBody List<Integer> imageIds) {
        imageService.batchDeleteImages(productId, imageIds);
        return Result.success();
    }
} 