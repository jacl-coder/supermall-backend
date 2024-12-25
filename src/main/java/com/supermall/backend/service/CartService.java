package com.supermall.backend.service;

import com.supermall.backend.dto.CartDTO;
import com.supermall.backend.vo.CartVO;

public interface CartService {
    CartVO addToCart(Long userId, CartDTO cartDTO);
    CartVO updateQuantity(Long userId, Long cartItemId, Integer quantity);
    CartVO updateSelected(Long userId, Long cartItemId, Integer selected);
    void removeFromCart(Long userId, Long cartItemId);
    CartVO getCart(Long userId);
    void clearCart(Long userId);
    CartVO updateAllSelected(Long userId, Integer selected);
}