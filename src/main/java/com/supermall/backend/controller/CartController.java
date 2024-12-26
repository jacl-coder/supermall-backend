package com.supermall.backend.controller;

import com.supermall.backend.common.api.CommonResult;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.cart.dto.CartDTO;
import com.supermall.backend.domain.cart.vo.CartVO;
import com.supermall.backend.domain.cart.service.CartService;
import com.supermall.backend.security.model.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车管理", description = "购物车管理接口")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @Operation(summary = "添加商品到购物车")
    @PostMapping
    public CommonResult<Void> add(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestBody AddCartParam param) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setProductId(param.getProductId());
        cartDTO.setQuantity(param.getQuantity());
        cartDTO.setChecked(true);
        cartService.add(getUserId(userDetails), cartDTO);
        return CommonResult.success(null);
    }

    @Operation(summary = "更新购物车商品数量")
    @PutMapping("/{productId}/quantity")
    public CommonResult<Void> updateQuantity(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long productId,
                                           @RequestBody UpdateQuantityParam param) {
        cartService.updateQuantity(getUserId(userDetails), productId, param.getQuantity());
        return CommonResult.success(null);
    }

    @Operation(summary = "更新商品选中状态")
    @PutMapping("/{productId}/checked")
    public CommonResult<Void> updateChecked(@AuthenticationPrincipal UserDetails userDetails,
                                          @PathVariable Long productId,
                                          @RequestBody UpdateCheckedParam param) {
        cartService.updateChecked(getUserId(userDetails), productId, param.getChecked() == 1);
        return CommonResult.success(null);
    }

    @Operation(summary = "批量更新商品选中状态")
    @PutMapping("/checked")
    public CommonResult<Void> updateCheckedAll(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestBody UpdateCheckedParam param) {
        cartService.updateCheckedAll(getUserId(userDetails), param.getChecked() == 1);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/{productId}")
    public CommonResult<Void> delete(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable Long productId) {
        cartService.delete(getUserId(userDetails), productId);
        return CommonResult.success(null);
    }

    @Operation(summary = "获取购物车列表")
    @GetMapping
    public CommonResult<List<CartVO>> list(@AuthenticationPrincipal UserDetails userDetails) {
        return CommonResult.success(cartService.list(getUserId(userDetails)));
    }

    @Operation(summary = "获取购物车商品数量")
    @GetMapping("/count")
    public CommonResult<Integer> count(@AuthenticationPrincipal UserDetails userDetails) {
        List<CartVO> cartList = cartService.list(getUserId(userDetails));
        int totalQuantity = cartList.stream()
                .mapToInt(CartVO::getQuantity)
                .sum();
        return CommonResult.success(totalQuantity);
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping
    public CommonResult<Void> clear(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clear(getUserId(userDetails));
        return CommonResult.success(null);
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getId();
        }
        throw new BusinessException("用户未登录");
    }

    @Data
    public static class AddCartParam {
        private Long productId;
        private Integer quantity;
    }

    @Data
    public static class UpdateQuantityParam {
        private Integer quantity;
    }

    @Data
    public static class UpdateCheckedParam {
        private Integer checked;
    }
} 