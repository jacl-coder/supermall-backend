package com.supermall.backend.domain.user.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserFavoriteResponse {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private LocalDateTime createdAt;
    
    // 商品信息
    private String productName;
    private String productDescription;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String mainImage;
    private Integer stock;
    private Integer sales;
} 