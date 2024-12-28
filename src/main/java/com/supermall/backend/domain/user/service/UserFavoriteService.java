package com.supermall.backend.domain.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.user.dto.FavoriteResponse;
import com.supermall.backend.domain.user.entity.UserFavorite;

public interface UserFavoriteService extends IService<UserFavorite> {
    void addFavorite(Integer userId, Integer productId);
    void removeFavorite(Integer userId, Integer productId);
    Page<FavoriteResponse> getFavorites(Integer userId, int page, int size);
    boolean isFavorite(Integer userId, Integer productId);
} 