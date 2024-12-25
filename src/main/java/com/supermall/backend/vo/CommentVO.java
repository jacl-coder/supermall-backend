package com.supermall.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {
    private Long id;
    private Long userId;
    private String username;
    private String userAvatar;
    private Long productId;
    private String content;
    private Integer rating;
    private List<String> images;
    private LocalDateTime createTime;
    private String productName;
} 