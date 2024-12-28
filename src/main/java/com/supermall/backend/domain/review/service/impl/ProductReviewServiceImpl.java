package com.supermall.backend.domain.review.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.order.entity.OrderItem;
import com.supermall.backend.domain.order.service.OrderService;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.service.ProductService;
import com.supermall.backend.domain.review.dto.ProductReviewRequest;
import com.supermall.backend.domain.review.dto.ProductReviewResponse;
import com.supermall.backend.domain.review.entity.ProductReview;
import com.supermall.backend.domain.review.exception.ReviewException;
import com.supermall.backend.domain.review.mapper.ProductReviewMapper;
import com.supermall.backend.domain.review.service.ProductReviewService;
import com.supermall.backend.domain.user.entity.UserProfile;
import com.supermall.backend.domain.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewServiceImpl extends ServiceImpl<ProductReviewMapper, ProductReview> implements ProductReviewService {

    private final OrderService orderService;
    private final ProductService productService;
    private final UserProfileService userProfileService;

    @Override
    @Transactional
    public ProductReviewResponse createReview(Integer userId, ProductReviewRequest request) {
        // 检查订单商品是否存在且属于当前用户
        OrderItem orderItem = orderService.getOrderItem(request.getOrderItemId());
        if (orderItem == null || !orderService.isUserOrder(userId, orderItem.getOrderId())) {
            throw new ReviewException("订单商品不存在或不属于当前用户");
        }

        // 检查是否已经评价过
        if (exists(new LambdaQueryWrapper<ProductReview>()
                .eq(ProductReview::getOrderItemId, request.getOrderItemId()))) {
            throw new ReviewException(ReviewException.ALREADY_REVIEWED);
        }

        // 检查评分是否有效
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new ReviewException(ReviewException.INVALID_RATING);
        }

        // 创建评价
        ProductReview review = new ProductReview();
        review.setOrderId(orderItem.getOrderId());
        review.setOrderItemId(request.getOrderItemId());
        review.setUserId(userId);
        review.setProductId(orderItem.getProductId());
        review.setMerchantId(orderItem.getMerchantId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages() != null ? String.join(",", request.getImages()) : null);
        review.setStatus(ProductReview.Status.PENDING);

        save(review);

        return convertToResponse(review);
    }

    @Override
    public Page<ProductReviewResponse> getProductReviews(Integer productId, int page, int size) {
        Page<ProductReview> reviewPage = page(new Page<>(page, size),
                new LambdaQueryWrapper<ProductReview>()
                        .eq(ProductReview::getProductId, productId)
                        .eq(ProductReview::getStatus, ProductReview.Status.PUBLISHED)
                        .orderByDesc(ProductReview::getCreatedAt));

        return convertToResponsePage(reviewPage);
    }

    @Override
    public Page<ProductReviewResponse> getUserReviews(Integer userId, int page, int size) {
        Page<ProductReview> reviewPage = page(new Page<>(page, size),
                new LambdaQueryWrapper<ProductReview>()
                        .eq(ProductReview::getUserId, userId)
                        .ne(ProductReview::getStatus, ProductReview.Status.DELETED)
                        .orderByDesc(ProductReview::getCreatedAt));

        return convertToResponsePage(reviewPage);
    }

    @Override
    public Page<ProductReviewResponse> getMerchantReviews(Integer merchantId, int page, int size) {
        Page<ProductReview> reviewPage = page(new Page<>(page, size),
                new LambdaQueryWrapper<ProductReview>()
                        .eq(ProductReview::getMerchantId, merchantId)
                        .ne(ProductReview::getStatus, ProductReview.Status.DELETED)
                        .orderByDesc(ProductReview::getCreatedAt));

        return convertToResponsePage(reviewPage);
    }

    @Override
    @Transactional
    public void deleteReview(Integer userId, Integer reviewId) {
        ProductReview review = getById(reviewId);
        if (review == null) {
            throw new ReviewException(ReviewException.REVIEW_NOT_FOUND);
        }
        if (!review.getUserId().equals(userId)) {
            throw new ReviewException(ReviewException.NO_PERMISSION);
        }
        if (review.getStatus() == ProductReview.Status.DELETED) {
            throw new ReviewException("评价已删除");
        }

        review.setStatus(ProductReview.Status.DELETED);
        updateById(review);
    }

    @Override
    @Transactional
    public void approveReview(Integer reviewId) {
        ProductReview review = getById(reviewId);
        if (review == null) {
            throw new ReviewException(ReviewException.REVIEW_NOT_FOUND);
        }
        if (review.getStatus() != ProductReview.Status.PENDING) {
            throw new ReviewException("评价状态不是待审核");
        }

        review.setStatus(ProductReview.Status.PUBLISHED);
        updateById(review);
    }

    @Override
    @Transactional
    public void rejectReview(Integer reviewId, String reason) {
        ProductReview review = getById(reviewId);
        if (review == null) {
            throw new ReviewException(ReviewException.REVIEW_NOT_FOUND);
        }
        if (review.getStatus() != ProductReview.Status.PENDING) {
            throw new ReviewException("评价状态不是待审核");
        }

        review.setStatus(ProductReview.Status.REJECTED);
        review.setRejectReason(reason);
        updateById(review);
    }

    @Override
    public Page<ProductReviewResponse> getPendingReviews(int page, int size) {
        Page<ProductReview> reviewPage = page(new Page<>(page, size),
                new LambdaQueryWrapper<ProductReview>()
                        .eq(ProductReview::getStatus, ProductReview.Status.PENDING)
                        .orderByDesc(ProductReview::getCreatedAt));

        return convertToResponsePage(reviewPage);
    }

    @Override
    public double getProductAverageRating(Integer productId) {
        List<ProductReview> reviews = list(new LambdaQueryWrapper<ProductReview>()
                .eq(ProductReview::getProductId, productId)
                .eq(ProductReview::getStatus, ProductReview.Status.PUBLISHED)
                .select(ProductReview::getRating));

        if (reviews.isEmpty()) {
            return 0.0;
        }

        double totalRating = reviews.stream()
                .mapToInt(ProductReview::getRating)
                .sum();

        return totalRating / reviews.size();
    }

    @Override
    public int getProductReviewCount(Integer productId) {
        return (int) count(new LambdaQueryWrapper<ProductReview>()
                .eq(ProductReview::getProductId, productId)
                .eq(ProductReview::getStatus, ProductReview.Status.PUBLISHED));
    }

    @Override
    public ProductReview getById(Integer reviewId) {
        return baseMapper.selectById(reviewId);
    }

    private ProductReviewResponse convertToResponse(ProductReview review) {
        ProductReviewResponse response = new ProductReviewResponse();
        BeanUtils.copyProperties(review, response);

        // 设置图片列表
        if (review.getImages() != null) {
            response.setImages(Arrays.asList(review.getImages().split(",")));
        }

        // 设置用户信息
        try {
            var userProfile = userProfileService.getProfile(review.getUserId());
            response.setUserName(userProfile.getFullName());
            response.setUserAvatar(userProfile.getAvatarUrl());
        } catch (BusinessException e) {
            // 用户资料不存在，使用默认值
            response.setUserName("未知用户");
            response.setUserAvatar(null);
        }

        // 设置商品信息
        Product product = productService.getById(review.getProductId());
        if (product != null) {
            response.setProductName(product.getName());
            response.setProductImage(product.getMainImage());
        }

        return response;
    }

    private Page<ProductReviewResponse> convertToResponsePage(Page<ProductReview> page) {
        Page<ProductReviewResponse> responsePage = new Page<>();
        responsePage.setRecords(page.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
        responsePage.setTotal(page.getTotal());
        responsePage.setCurrent(page.getCurrent());
        responsePage.setSize(page.getSize());
        return responsePage;
    }
} 