package com.supermall.backend.domain.review.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.merchant.dto.MerchantResponse;
import com.supermall.backend.domain.merchant.service.MerchantProfileService;
import com.supermall.backend.domain.review.dto.ReviewReplyRequest;
import com.supermall.backend.domain.review.dto.ReviewReplyResponse;
import com.supermall.backend.domain.review.entity.ProductReview;
import com.supermall.backend.domain.review.entity.ReviewReply;
import com.supermall.backend.domain.review.exception.ReviewException;
import com.supermall.backend.domain.review.mapper.ReviewReplyMapper;
import com.supermall.backend.domain.review.service.ProductReviewService;
import com.supermall.backend.domain.review.service.ReviewReplyService;
import com.supermall.backend.domain.user.dto.UserProfileResponse;
import com.supermall.backend.domain.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewReplyServiceImpl extends ServiceImpl<ReviewReplyMapper, ReviewReply> implements ReviewReplyService {

    private final ProductReviewService reviewService;
    private final UserProfileService userProfileService;
    private final MerchantProfileService merchantProfileService;

    @Override
    @Transactional
    public ReviewReplyResponse createReply(Integer userId, Integer reviewId, ReviewReplyRequest request) {
        // 检查评论是否存在且已发布
        ProductReview review = reviewService.getById(reviewId);
        if (review == null || review.getStatus() != ProductReview.Status.PUBLISHED) {
            throw new ReviewException("评论不存在或未发布");
        }

        // 创建回复
        ReviewReply reply = new ReviewReply();
        reply.setReviewId(reviewId);
        reply.setUserId(userId);
        reply.setContent(request.getContent());
        reply.setStatus(ReviewReply.Status.PUBLISHED);

        save(reply);

        return convertToResponse(reply);
    }

    @Override
    public Page<ReviewReplyResponse> getReviewReplies(Integer reviewId, int page, int size) {
        Page<ReviewReply> replyPage = page(new Page<>(page, size),
                new LambdaQueryWrapper<ReviewReply>()
                        .eq(ReviewReply::getReviewId, reviewId)
                        .eq(ReviewReply::getStatus, ReviewReply.Status.PUBLISHED)
                        .orderByAsc(ReviewReply::getCreatedAt));

        Page<ReviewReplyResponse> responsePage = new Page<>();
        responsePage.setRecords(replyPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
        responsePage.setTotal(replyPage.getTotal());
        responsePage.setCurrent(replyPage.getCurrent());
        responsePage.setSize(replyPage.getSize());
        return responsePage;
    }

    @Override
    @Transactional
    public void deleteReply(Integer userId, Integer replyId) {
        ReviewReply reply = getById(replyId);
        if (reply == null) {
            throw new ReviewException("回复不存在");
        }
        if (!reply.getUserId().equals(userId) && !reply.getMerchantId().equals(userId)) {
            throw new ReviewException("无权���除此回复");
        }
        if (reply.getStatus() == ReviewReply.Status.DELETED) {
            throw new ReviewException("回复已删除");
        }

        reply.setStatus(ReviewReply.Status.DELETED);
        updateById(reply);
    }

    @Override
    @Transactional
    public ReviewReplyResponse merchantReply(Integer merchantId, Integer reviewId, ReviewReplyRequest request) {
        // 检查评论是否存在且已发布
        ProductReview review = reviewService.getById(reviewId);
        if (review == null || review.getStatus() != ProductReview.Status.PUBLISHED) {
            throw new ReviewException("评论不存在或未发布");
        }

        // 检查评论是否属于该商家
        if (!review.getMerchantId().equals(merchantId)) {
            throw new ReviewException("无权回复此评论");
        }

        // 创建商家回复
        ReviewReply reply = new ReviewReply();
        reply.setReviewId(reviewId);
        reply.setMerchantId(merchantId);
        reply.setContent(request.getContent());
        reply.setStatus(ReviewReply.Status.PUBLISHED);

        save(reply);

        return convertToResponse(reply);
    }

    private ReviewReplyResponse convertToResponse(ReviewReply reply) {
        ReviewReplyResponse response = new ReviewReplyResponse();
        BeanUtils.copyProperties(reply, response);

        // 设置用户信息
        if (reply.getUserId() != null) {
            try {
                UserProfileResponse userProfile = userProfileService.getProfile(reply.getUserId());
                response.setUserName(userProfile.getFullName());
                response.setUserAvatar(userProfile.getAvatarUrl());
            } catch (BusinessException e) {
                response.setUserName("未知用户");
                response.setUserAvatar(null);
            }
        }

        // 设置商家信息
        if (reply.getMerchantId() != null) {
            try {
                MerchantResponse merchantProfile = merchantProfileService.getProfile(reply.getMerchantId());
                response.setMerchantName(merchantProfile.getShopName());
            } catch (BusinessException e) {
                response.setMerchantName("未知商家");
            }
        }

        return response;
    }
} 