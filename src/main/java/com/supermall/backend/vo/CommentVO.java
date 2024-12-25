package com.supermall.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {
    private Long id;
    private Long userId;
    private String username;  // 评论用户名
    private String userAvatar;  // 用户头像
    private Long productId;
    private String productName;  // 商品名称
    private String content;
    private Integer rating;
    private List<String> images;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 