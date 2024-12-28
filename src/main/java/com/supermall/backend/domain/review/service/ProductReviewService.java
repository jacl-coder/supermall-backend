package com.supermall.backend.domain.review.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.review.dto.ProductReviewRequest;
import com.supermall.backend.domain.review.dto.ProductReviewResponse;
import com.supermall.backend.domain.review.entity.ProductReview;

public interface ProductReviewService {
    ProductReviewResponse createReview(Integer userId, ProductReviewRequest request);
    Page<ProductReviewResponse> getProductReviews(Integer productId, int page, int size);
    Page<ProductReviewResponse> getUserReviews(Integer userId, int page, int size);
    Page<ProductReviewResponse> getMerchantReviews(Integer merchantId, int page, int size);
    void deleteReview(Integer userId, Integer reviewId);
    
    // 评论审核相关方法
    void approveReview(Integer reviewId);
    void rejectReview(Integer reviewId, String reason);
    Page<ProductReviewResponse> getPendingReviews(int page, int size);
    
    // 评分统计相关方法
    double getProductAverageRating(Integer productId);
    int getProductReviewCount(Integer productId);
    
    // 获取评论详情
    ProductReview getById(Integer reviewId);
} 