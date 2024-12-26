package com.supermall.backend.controller;

import com.supermall.backend.common.api.CommonResult;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.cart.service.CartService;
import com.supermall.backend.domain.cart.vo.CartVO;
import com.supermall.backend.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "购物车管理", description = "购物车管理接口")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    @Operation(summary = "添加商品到购物车")
    @PostMapping
    public CommonResult<CartVO> addToCart(@RequestBody CartVO cartVO) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            log.debug("添加商品到购物车: userId={}, cartVO={}", userId, cartVO);
            CartVO cart = cartService.addToCart(userId, cartVO.getProductId(), cartVO.getQuantity());
            return CommonResult.success(cart);
        } catch (ApiException e) {
            log.error("添加商品到购物车失败: {}", e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("添加商品到购物车失败", e);
            return CommonResult.failed("添加商品到购物车失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取购物车列表")
    @GetMapping
    public CommonResult<List<CartVO>> list() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            List<CartVO> cartList = cartService.listUserCart(userId);
            return CommonResult.success(cartList);
        } catch (ApiException e) {
            log.error("获取购物车列表失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("获取购物车列表失败", e);
            return CommonResult.failed("获取购物车列表失败");
        }
    }
    
    @Operation(summary = "更新购物车商品数量")
    @PutMapping("/{id}/quantity")
    public CommonResult<Void> updateQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        try {
            cartService.updateQuantity(id, quantity);
            return CommonResult.success(null);
        } catch (ApiException e) {
            log.error("更新购物车商品数量失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("更新购物车商品数量失败", e);
            return CommonResult.failed("更新购物车商品数量失败");
        }
    }
    
    @Operation(summary = "更新购物车商品选中状态")
    @PutMapping("/{id}/checked")
    public CommonResult<Void> updateChecked(
            @PathVariable Long id,
            @RequestParam Boolean checked) {
        try {
            cartService.updateChecked(id, checked);
            return CommonResult.success(null);
        } catch (ApiException e) {
            log.error("更新购物车商品选中状态失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("更新购物车商品选中状态失败", e);
            return CommonResult.failed("更新购物车商品选中状态失败");
        }
    }
    
    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(@PathVariable Long id) {
        try {
            cartService.deleteCartItem(id);
            return CommonResult.success(null);
        } catch (ApiException e) {
            log.error("删除购物车商品失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("删除购物车商品失败", e);
            return CommonResult.failed("删除购物车商品失败");
        }
    }
    
    @Operation(summary = "清空购物车")
    @DeleteMapping("/clear")
    public CommonResult<Void> clear() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            cartService.clearCart(userId);
            return CommonResult.success(null);
        } catch (ApiException e) {
            log.error("清空购物车失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("清空购物车失败", e);
            return CommonResult.failed("清空购物车失败");
        }
    }
} 