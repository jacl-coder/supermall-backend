package com.supermall.backend.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "评论回复请求")
public class ReviewReplyRequest {
    @Schema(description = "回复内容", required = true)
    private String content;
} 