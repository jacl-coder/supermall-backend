package com.supermall.backend.domain.cart.service;

import com.supermall.backend.domain.cart.dto.CartDTO;
import com.supermall.backend.domain.cart.vo.CartVO;
import java.util.List;

public interface CartService {
    /**
     * 添加商品到购物车
     */
    void add(Long userId, CartDTO cartDTO);
    
    /**
     * 更新购物车商品数量
     */
    void updateQuantity(Long userId, Long productId, Integer quantity);
    
    /**
     * 更新购物车商品选中状态
     */
    void updateChecked(Long userId, Long productId, Boolean checked);
    
    /**
     * 批量更新购物车商品选中状态
     */
    void updateCheckedAll(Long userId, Boolean checked);
    
    /**
     * 删除购物车商品
     */
    void delete(Long userId, Long productId);
    
    /**
     * 清空购物车
     */
    void clear(Long userId);
    
    /**
     * 获取用户购物车列表
     */
    List<CartVO> list(Long userId);
} 