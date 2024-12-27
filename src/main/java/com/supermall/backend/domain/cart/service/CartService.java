package com.supermall.backend.domain.cart.service;

import com.supermall.backend.domain.cart.dto.CartItemRequest;
import com.supermall.backend.domain.cart.dto.CartItemResponse;
import com.supermall.backend.domain.cart.entity.CartItem;
import java.util.List;

public interface CartService {
    CartItemResponse addToCart(Long userId, CartItemRequest request);
    CartItemResponse updateQuantity(Long userId, Long itemId, Integer quantity);
    void removeFromCart(Long userId, Long itemId);
    void updateSelected(Long userId, Long itemId, Boolean selected);
    void updateAllSelected(Long userId, Boolean selected);
    List<CartItemResponse> getUserCart(Long userId);
    void clearCart(Long userId);
    List<CartItem> getByIds(List<Long> ids);
    void deleteByIds(List<Long> ids);
} 