package com.supermall.backend.domain.cart.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.common.security.annotation.RequirePermission;
import com.supermall.backend.common.security.model.SecurityUser;
import com.supermall.backend.domain.cart.dto.CartItemRequest;
import com.supermall.backend.domain.cart.dto.CartItemResponse;
import com.supermall.backend.domain.cart.dto.BatchUpdateQuantityRequest;
import com.supermall.backend.domain.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车管理", description = "包括购物车商品的增删改查等功能")
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "添加商品到购物车")
    @PostMapping
    @RequirePermission(role = "USER")
    public Result<CartItemResponse> addToCart(
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(cartService.addToCart(user.getId(), request));
    }

    @Operation(summary = "更新购物车商品数量")
    @PutMapping("/{itemId}/quantity/{quantity}")
    @RequirePermission(role = "USER")
    public Result<CartItemResponse> updateQuantity(
            @PathVariable Integer itemId,
            @PathVariable Integer quantity,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(cartService.updateQuantity(user.getId(), itemId, quantity));
    }

    @Operation(summary = "从购物车移除商品")
    @DeleteMapping("/{itemId}")
    @RequirePermission(role = "USER")
    public Result<Void> removeFromCart(
            @PathVariable Integer itemId,
            @AuthenticationPrincipal SecurityUser user) {
        cartService.removeFromCart(user.getId(), itemId);
        return Result.success(null);
    }

    @Operation(summary = "更新商品选中状态")
    @PutMapping("/{itemId}/selected/{selected}")
    @RequirePermission(role = "USER")
    public Result<Void> updateSelected(
            @PathVariable Integer itemId,
            @PathVariable Boolean selected,
            @AuthenticationPrincipal SecurityUser user) {
        cartService.updateSelected(user.getId(), itemId, selected);
        return Result.success(null);
    }

    @Operation(summary = "更新全部商品选中状态")
    @PutMapping("/selected/{selected}")
    @RequirePermission(role = "USER")
    public Result<Void> updateAllSelected(
            @PathVariable Boolean selected,
            @AuthenticationPrincipal SecurityUser user) {
        cartService.updateAllSelected(user.getId(), selected);
        return Result.success(null);
    }

    @Operation(summary = "获取购物车列表")
    @GetMapping
    @RequirePermission(role = "USER")
    public Result<List<CartItemResponse>> getUserCart(
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(cartService.getUserCart(user.getId()));
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping
    @RequirePermission(role = "USER")
    public Result<Void> clearCart(
            @AuthenticationPrincipal SecurityUser user) {
        cartService.clearCart(user.getId());
        return Result.success(null);
    }

    @Operation(summary = "批量删除购物车商品")
    @DeleteMapping("/batch")
    @RequirePermission(role = "USER")
    public Result<Void> batchRemoveFromCart(
            @RequestParam List<Integer> itemIds,
            @AuthenticationPrincipal SecurityUser user) {
        cartService.batchRemoveFromCart(user.getId(), itemIds);
        return Result.success(null);
    }

    @Operation(summary = "批量更新商品选中状态")
    @PutMapping("/batch/selected/{selected}")
    @RequirePermission(role = "USER")
    public Result<Void> batchUpdateSelected(
            @RequestParam List<Integer> itemIds,
            @PathVariable Boolean selected,
            @AuthenticationPrincipal SecurityUser user) {
        cartService.batchUpdateSelected(user.getId(), itemIds, selected);
        return Result.success(null);
    }

    @Operation(summary = "批量更新商品数量")
    @PutMapping("/batch/quantity")
    @RequirePermission(role = "USER")
    public Result<Void> batchUpdateQuantity(
            @Valid @RequestBody BatchUpdateQuantityRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        cartService.batchUpdateQuantity(user.getId(), request.getItemIds(), request.getQuantities());
        return Result.success(null);
    }
} 