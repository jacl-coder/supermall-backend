package com.supermall.backend.domain.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.user.dto.FavoriteResponse;
import com.supermall.backend.domain.user.service.UserFavoriteService;
import com.supermall.backend.common.security.model.SecurityUser;
import com.supermall.backend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/favorites")
@RequiredArgsConstructor
public class UserFavoriteController {

    private final UserFavoriteService favoriteService;

    @PostMapping("/products/{productId}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> addFavorite(
            @PathVariable Integer productId,
            @AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            throw new BusinessException("请先登录");
        }
        favoriteService.addFavorite(user.getId(), productId);
        return Result.success();
    }

    @DeleteMapping("/products/{productId}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> removeFavorite(
            @PathVariable Integer productId,
            @AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            throw new BusinessException("请先登录");
        }
        favoriteService.removeFavorite(user.getId(), productId);
        return Result.success();
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Result<Page<FavoriteResponse>> getFavorites(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            throw new BusinessException("请先登录");
        }
        Page<FavoriteResponse> favorites = favoriteService.getFavorites(user.getId(), page, size);
        return Result.success(favorites);
    }
} 