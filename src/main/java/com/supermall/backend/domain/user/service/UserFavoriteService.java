package com.supermall.backend.domain.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.user.dto.UserFavoriteResponse;

public interface UserFavoriteService {
    /**
     * 添加收藏
     */
    UserFavoriteResponse addFavorite(Long userId, Long productId);
    
    /**
     * 取消收藏
     */
    void removeFavorite(Long userId, Long productId);
    
    /**
     * 获取用户收藏列表
     */
    Page<UserFavoriteResponse> getUserFavorites(Long userId, int page, int size);
    
    /**
     * 检查是否已收藏
     */
    boolean isFavorite(Long userId, Long productId);
    
    /**
     * 获取收藏数量
     */
    Long getFavoriteCount(Long userId);
} 