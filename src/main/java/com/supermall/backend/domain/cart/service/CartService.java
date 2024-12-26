package com.supermall.backend.domain.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.cart.entity.Cart;
import com.supermall.backend.domain.cart.vo.CartVO;
import java.util.List;

public interface CartService extends IService<Cart> {
    /**
     * 添加商品到购物车
     */
    CartVO addToCart(Long userId, Long productId, Integer quantity);
    
    /**
     * 获取用户购物车列表
     */
    List<CartVO> listUserCart(Long userId);
    
    /**
     * 更新购物车商品数量
     */
    void updateQuantity(Long id, Integer quantity);
    
    /**
     * 更新购物车商品选中状态
     */
    void updateChecked(Long id, Boolean checked);
    
    /**
     * 删除购物车商品
     */
    void deleteCartItem(Long id);
    
    /**
     * 清空用户购物车
     */
    void clearCart(Long userId);
} 