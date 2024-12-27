package com.supermall.backend.domain.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.user.dto.UserFavoriteResponse;
import com.supermall.backend.domain.user.service.UserFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户收藏管理", description = "包括商品收藏、收藏列表查询、取消收藏等功能")
@RestController
@RequestMapping("/api/users/favorites")
@RequiredArgsConstructor
public class UserFavoriteController {

    private final UserFavoriteService favoriteService;

    @Operation(summary = "添加收藏")
    @PostMapping("/{productId}")
    public ResponseEntity<UserFavoriteResponse> addFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.addFavorite(
                Long.valueOf(userDetails.getUsername()),
                productId
        ));
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        favoriteService.removeFavorite(
                Long.valueOf(userDetails.getUsername()),
                productId
        );
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "获取收藏列表")
    @GetMapping
    public ResponseEntity<Page<UserFavoriteResponse>> getFavorites(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(
                Long.valueOf(userDetails.getUsername()),
                page,
                size
        ));
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/{productId}/check")
    public ResponseEntity<Boolean> checkFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.isFavorite(
                Long.valueOf(userDetails.getUsername()),
                productId
        ));
    }

    @Operation(summary = "获取收藏数量")
    @GetMapping("/count")
    public ResponseEntity<Long> getFavoriteCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.getFavoriteCount(
                Long.valueOf(userDetails.getUsername())
        ));
    }
} 