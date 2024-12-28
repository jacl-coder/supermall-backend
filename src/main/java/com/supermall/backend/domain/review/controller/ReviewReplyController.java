package com.supermall.backend.domain.review.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.security.annotation.RequirePermission;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.common.security.util.SecurityUtil;
import com.supermall.backend.domain.review.dto.ReviewReplyRequest;
import com.supermall.backend.domain.review.dto.ReviewReplyResponse;
import com.supermall.backend.domain.review.service.ReviewReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评论回复")
@RestController
@RequestMapping("/api/reviews/{reviewId}/replies")
@RequiredArgsConstructor
public class ReviewReplyController {

    private final ReviewReplyService replyService;
    private final SecurityUtil securityUtil;

    @Operation(summary = "创建评论回复")
    @PostMapping
    @RequirePermission(role = "USER")
    public Result<ReviewReplyResponse> createReply(
            @PathVariable Integer reviewId,
            @RequestBody ReviewReplyRequest request) {
        return Result.success(replyService.createReply(securityUtil.getCurrentUserId(), reviewId, request));
    }

    @Operation(summary = "获取评论回复列表")
    @GetMapping
    public Result<Page<ReviewReplyResponse>> getReviewReplies(
            @PathVariable Integer reviewId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(replyService.getReviewReplies(reviewId, page, size));
    }

    @Operation(summary = "删除评论回复")
    @DeleteMapping("/{replyId}")
    @RequirePermission(role = "USER")
    public Result<Void> deleteReply(
            @PathVariable Integer reviewId,
            @PathVariable Integer replyId) {
        replyService.deleteReply(securityUtil.getCurrentUserId(), replyId);
        return Result.success();
    }

    @Operation(summary = "商家回复评论")
    @PostMapping("/merchant")
    @RequirePermission(role = "MERCHANT")
    public Result<ReviewReplyResponse> merchantReply(
            @PathVariable Integer reviewId,
            @RequestBody ReviewReplyRequest request) {
        return Result.success(replyService.merchantReply(securityUtil.getCurrentUserId(), reviewId, request));
    }
} 