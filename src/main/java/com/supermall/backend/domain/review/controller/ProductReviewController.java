package com.supermall.backend.domain.review.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.security.annotation.RequirePermission;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.common.security.util.SecurityUtil;
import com.supermall.backend.domain.review.dto.ProductReviewRequest;
import com.supermall.backend.domain.review.dto.ProductReviewResponse;
import com.supermall.backend.domain.review.service.ProductReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品评价")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService reviewService;
    private final SecurityUtil securityUtil;

    @Operation(summary = "创建商品评价")
    @PostMapping
    @RequirePermission(role = "USER")
    public Result<ProductReviewResponse> createReview(@RequestBody ProductReviewRequest request) {
        return Result.success(reviewService.createReview(securityUtil.getCurrentUserId(), request));
    }

    @Operation(summary = "获取商品评价列表")
    @GetMapping("/products/{productId}")
    public Result<Page<ProductReviewResponse>> getProductReviews(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(reviewService.getProductReviews(productId, page, size));
    }

    @Operation(summary = "获取用户评价列表")
    @GetMapping("/users")
    @RequirePermission(role = "USER")
    public Result<Page<ProductReviewResponse>> getUserReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(reviewService.getUserReviews(securityUtil.getCurrentUserId(), page, size));
    }

    @Operation(summary = "获取商家评价列表")
    @GetMapping("/merchants/{merchantId}")
    @RequirePermission(role = "MERCHANT")
    public Result<Page<ProductReviewResponse>> getMerchantReviews(
            @PathVariable Integer merchantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(reviewService.getMerchantReviews(merchantId, page, size));
    }

    @Operation(summary = "删除评价")
    @DeleteMapping("/{reviewId}")
    @RequirePermission(role = "USER")
    public Result<Void> deleteReview(@PathVariable Integer reviewId) {
        reviewService.deleteReview(securityUtil.getCurrentUserId(), reviewId);
        return Result.success();
    }

    @Operation(summary = "获取待审核评价列表")
    @GetMapping("/pending")
    @RequirePermission(role = "ADMIN")
    public Result<Page<ProductReviewResponse>> getPendingReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(reviewService.getPendingReviews(page, size));
    }

    @Operation(summary = "审核通过评价")
    @PostMapping("/{reviewId}/approve")
    @RequirePermission(role = "ADMIN")
    public Result<Void> approveReview(@PathVariable Integer reviewId) {
        reviewService.approveReview(reviewId);
        return Result.success();
    }

    @Operation(summary = "拒绝评价")
    @PostMapping("/{reviewId}/reject")
    @RequirePermission(role = "ADMIN")
    public Result<Void> rejectReview(
            @PathVariable Integer reviewId,
            @RequestParam String reason) {
        reviewService.rejectReview(reviewId, reason);
        return Result.success();
    }

    @Operation(summary = "获取商品评分统计")
    @GetMapping("/products/{productId}/stats")
    public Result<ProductReviewStats> getProductReviewStats(@PathVariable Integer productId) {
        ProductReviewStats stats = new ProductReviewStats();
        stats.setAverageRating(reviewService.getProductAverageRating(productId));
        stats.setReviewCount(reviewService.getProductReviewCount(productId));
        return Result.success(stats);
    }
}

@Data
class ProductReviewStats {
    private double averageRating;
    private int reviewCount;
} 