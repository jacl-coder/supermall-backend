package com.supermall.backend.domain.cart.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.cart.dto.CartItemRequest;
import com.supermall.backend.domain.cart.dto.CartItemResponse;
import com.supermall.backend.domain.cart.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车管理", description = "包括购物车商品的添加、删除、修改数量、清空购物车、查询购物车列表等功能")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public Result<CartItemResponse> addToCart(@Valid @RequestBody CartItemRequest request) {
        // TODO: 从SecurityContext获取用户ID
        Long userId = 1L;
        return Result.success(cartService.addToCart(userId, request));
    }

    @PutMapping("/{itemId}/quantity/{quantity}")
    public Result<CartItemResponse> updateQuantity(@PathVariable Long itemId, @PathVariable Integer quantity) {
        Long userId = 1L;
        return Result.success(cartService.updateQuantity(userId, itemId, quantity));
    }

    @DeleteMapping("/{itemId}")
    public Result<Void> removeFromCart(@PathVariable Long itemId) {
        Long userId = 1L;
        cartService.removeFromCart(userId, itemId);
        return Result.success(null);
    }

    @PutMapping("/{itemId}/selected/{selected}")
    public Result<Void> updateSelected(@PathVariable Long itemId, @PathVariable Boolean selected) {
        Long userId = 1L;
        cartService.updateSelected(userId, itemId, selected);
        return Result.success(null);
    }

    @PutMapping("/selected/{selected}")
    public Result<Void> updateAllSelected(@PathVariable Boolean selected) {
        Long userId = 1L;
        cartService.updateAllSelected(userId, selected);
        return Result.success(null);
    }

    @GetMapping
    public Result<List<CartItemResponse>> getUserCart() {
        Long userId = 1L;
        return Result.success(cartService.getUserCart(userId));
    }

    @DeleteMapping
    public Result<Void> clearCart() {
        Long userId = 1L;
        cartService.clearCart(userId);
        return Result.success(null);
    }
} 