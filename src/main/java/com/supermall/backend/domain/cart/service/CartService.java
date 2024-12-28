package com.supermall.backend.domain.cart.service;

import com.supermall.backend.domain.cart.dto.CartItemRequest;
import com.supermall.backend.domain.cart.dto.CartItemResponse;
import com.supermall.backend.domain.cart.entity.CartItem;
import java.util.List;

public interface CartService {
    CartItemResponse addToCart(Integer userId, CartItemRequest request);
    CartItemResponse updateQuantity(Integer userId, Integer itemId, Integer quantity);
    void removeFromCart(Integer userId, Integer itemId);
    void updateSelected(Integer userId, Integer itemId, Boolean selected);
    void updateAllSelected(Integer userId, Boolean selected);
    List<CartItemResponse> getUserCart(Integer userId);
    void clearCart(Integer userId);
    List<CartItem> getByIds(List<Integer> ids);
    void deleteByIds(List<Integer> ids);
    
    // 批量操作方法
    void batchRemoveFromCart(Integer userId, List<Integer> itemIds);
    void batchUpdateSelected(Integer userId, List<Integer> itemIds, Boolean selected);
    void batchUpdateQuantity(Integer userId, List<Integer> itemIds, List<Integer> quantities);
} 