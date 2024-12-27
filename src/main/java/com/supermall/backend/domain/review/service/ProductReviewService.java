package com.supermall.backend.domain.review.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.review.dto.ProductReviewRequest;
import com.supermall.backend.domain.review.dto.ProductReviewResponse;
import com.supermall.backend.domain.review.entity.ProductReview;

public interface ProductReviewService {
    ProductReviewResponse createReview(Long userId, ProductReviewRequest request);
    Page<ProductReviewResponse> getProductReviews(Long productId, int page, int size);
    Page<ProductReviewResponse> getUserReviews(Long userId, int page, int size);
    Page<ProductReviewResponse> getMerchantReviews(Long merchantId, int page, int size);
    void deleteReview(Long userId, Long reviewId);
} 