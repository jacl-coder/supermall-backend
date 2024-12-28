package com.supermall.backend.domain.review.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.review.dto.ReviewReplyRequest;
import com.supermall.backend.domain.review.dto.ReviewReplyResponse;

public interface ReviewReplyService {
    // 创建回复
    ReviewReplyResponse createReply(Integer userId, Integer reviewId, ReviewReplyRequest request);
    
    // 获取评论的回复列表
    Page<ReviewReplyResponse> getReviewReplies(Integer reviewId, int page, int size);
    
    // 删除回复
    void deleteReply(Integer userId, Integer replyId);
    
    // 商家回复评论
    ReviewReplyResponse merchantReply(Integer merchantId, Integer reviewId, ReviewReplyRequest request);
} 