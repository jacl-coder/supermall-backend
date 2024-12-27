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
import com.supermall.backend.domain.review.mapper.ProductReviewMapper;
import com.supermall.backend.domain.review.service.ProductReviewService;
import com.supermall.backend.domain.auth.entity.UserProfile;
import com.supermall.backend.domain.auth.service.UserProfileService;
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
    public ProductReviewResponse createReview(Long userId, ProductReviewRequest request) {
        // 检查订单商品是否存在且属于当前用户
        OrderItem orderItem = orderService.getOrderItem(request.getOrderItemId());
        if (orderItem == null || !orderService.isUserOrder(userId, orderItem.getOrderId())) {
            throw new BusinessException("订单商品不存在或不属于当前用户");
        }

        // 检查是否已经评价过
        if (exists(new LambdaQueryWrapper<ProductReview>()
                .eq(ProductReview::getOrderItemId, request.getOrderItemId()))) {
            throw new BusinessException("该订单商品已评价");
        }

        // 创建评价
        ProductReview review = new ProductReview();
        review.setOrderItemId(request.getOrderItemId());
        review.setUserId(userId);
        review.setProductId(orderItem.getProductId());
        review.setMerchantId(orderItem.getMerchantId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages() != null ? String.join(",", request.getImages()) : null);
        review.setStatus("active");

        save(review);

        return convertToResponse(review);
    }

    @Override
    public Page<ProductReviewResponse> getProductReviews(Long productId, int page, int size) {
        Page<ProductReview> reviewPage = page(new Page<>(page, size),
                new LambdaQueryWrapper<ProductReview>()
                        .eq(ProductReview::getProductId, productId)
                        .eq(ProductReview::getStatus, "active")
                        .orderByDesc(ProductReview::getCreatedAt));

        return convertToResponsePage(reviewPage);
    }

    @Override
    public Page<ProductReviewResponse> getUserReviews(Long userId, int page, int size) {
        Page<ProductReview> reviewPage = page(new Page<>(page, size),
                new LambdaQueryWrapper<ProductReview>()
                        .eq(ProductReview::getUserId, userId)
                        .orderByDesc(ProductReview::getCreatedAt));

        return convertToResponsePage(reviewPage);
    }

    @Override
    public Page<ProductReviewResponse> getMerchantReviews(Long merchantId, int page, int size) {
        Page<ProductReview> reviewPage = page(new Page<>(page, size),
                new LambdaQueryWrapper<ProductReview>()
                        .eq(ProductReview::getMerchantId, merchantId)
                        .orderByDesc(ProductReview::getCreatedAt));

        return convertToResponsePage(reviewPage);
    }

    @Override
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        ProductReview review = getById(reviewId);
        if (review == null || !review.getUserId().equals(userId)) {
            throw new BusinessException("评价不存在或无权删除");
        }

        removeById(reviewId);
    }

    private ProductReviewResponse convertToResponse(ProductReview review) {
        ProductReviewResponse response = new ProductReviewResponse();
        BeanUtils.copyProperties(review, response);

        // 设置图片列表
        if (review.getImages() != null) {
            response.setImages(Arrays.asList(review.getImages().split(",")));
        }

        // 设置用户信息
        UserProfile userProfile = userProfileService.getByUserId(review.getUserId());
        if (userProfile != null) {
            response.setUserName(userProfile.getFullName());
            response.setUserAvatar(userProfile.getAvatarUrl());
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