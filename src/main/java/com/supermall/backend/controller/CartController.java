package com.supermall.backend.controller;

import com.supermall.backend.common.response.Result;
import com.supermall.backend.dto.CartDTO;
import com.supermall.backend.service.CartService;
import com.supermall.backend.vo.CartVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public Result<CartVO> addToCart(@Valid @RequestBody CartDTO cartDTO) {
        // 暂时使用固定用户ID
        Long userId = 1L;
        return Result.success(cartService.addToCart(userId, cartDTO));
    }

    @PutMapping("/{cartItemId}/quantity")
    public Result<CartVO> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        Long userId = 1L;
        return Result.success(cartService.updateQuantity(userId, cartItemId, quantity));
    }

    @PutMapping("/{cartItemId}/selected")
    public Result<CartVO> updateSelected(
            @PathVariable Long cartItemId,
            @RequestParam Integer selected) {
        Long userId = 1L;
        return Result.success(cartService.updateSelected(userId, cartItemId, selected));
    }

    @DeleteMapping("/{cartItemId}")
    public Result<Void> removeFromCart(@PathVariable Long cartItemId) {
        Long userId = 1L;
        cartService.removeFromCart(userId, cartItemId);
        return Result.success();
    }

    @GetMapping
    public Result<CartVO> getCart() {
        Long userId = 1L;
        return Result.success(cartService.getCart(userId));
    }

    @DeleteMapping
    public Result<Void> clearCart() {
        Long userId = 1L;
        cartService.clearCart(userId);
        return Result.success();
    }

    @PutMapping("/selected")
    public Result<CartVO> updateAllSelected(@RequestParam Integer selected) {
        Long userId = 1L;
        return Result.success(cartService.updateAllSelected(userId, selected));
    }
}