package com.supermall.backend.domain.review.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.review.dto.ProductReviewRequest;
import com.supermall.backend.domain.review.dto.ProductReviewResponse;
import com.supermall.backend.domain.review.service.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<ProductReviewResponse> createReview(@Valid @RequestBody ProductReviewRequest request) {
        // TODO: 从SecurityContext获取用户ID
        Long userId = 1L;
        return Result.success(productReviewService.createReview(userId, request));
    }

    @GetMapping("/product/{productId}")
    public Result<Page<ProductReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(productReviewService.getProductReviews(productId, page, size));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public Result<Page<ProductReviewResponse>> getUserReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // TODO: 从SecurityContext获取用户ID
        Long userId = 1L;
        return Result.success(productReviewService.getUserReviews(userId, page, size));
    }

    @GetMapping("/merchant/{merchantId}")
    public Result<Page<ProductReviewResponse>> getMerchantReviews(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(productReviewService.getMerchantReviews(merchantId, page, size));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deleteReview(@PathVariable Long reviewId) {
        // TODO: 从SecurityContext获取用户ID
        Long userId = 1L;
        productReviewService.deleteReview(userId, reviewId);
        return Result.success(null);
    }
} 