package com.supermall.backend.domain.review.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductReviewResponse {
    private Integer id;
    private Integer orderItemId;
    private Integer userId;
    private Integer productId;
    private Integer merchantId;
    private Integer rating;
    private String content;
    private List<String> images;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 额外的展示信息
    private String userName;
    private String userAvatar;
    private String productName;
    private String productImage;
} 