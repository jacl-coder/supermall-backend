package com.supermall.backend.domain.review.dto;

import com.supermall.backend.domain.review.entity.ReviewReply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "评论回复响应")
public class ReviewReplyResponse {
    @Schema(description = "回复ID")
    private Integer id;
    
    @Schema(description = "评论ID")
    private Integer reviewId;
    
    @Schema(description = "回复用户ID")
    private Integer userId;
    
    @Schema(description = "回复用户名称")
    private String userName;
    
    @Schema(description = "回复用户头像")
    private String userAvatar;
    
    @Schema(description = "商家ID")
    private Integer merchantId;
    
    @Schema(description = "商家名称")
    private String merchantName;
    
    @Schema(description = "回复内容")
    private String content;
    
    @Schema(description = "回复状态")
    private ReviewReply.Status status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
} 