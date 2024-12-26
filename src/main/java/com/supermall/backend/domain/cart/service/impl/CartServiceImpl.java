package com.supermall.backend.domain.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.cart.dto.CartDTO;
import com.supermall.backend.domain.cart.entity.Cart;
import com.supermall.backend.domain.cart.mapper.CartMapper;
import com.supermall.backend.domain.cart.service.CartService;
import com.supermall.backend.domain.cart.vo.CartVO;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    
    private final CartMapper cartMapper;
    private final ProductService productService;
    
    @Override
    @Transactional
    public void add(Long userId, CartDTO cartDTO) {
        // 检查商品是否存在
        Product product = productService.getById(cartDTO.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 检查商品是否上架
        if (product.getStatus() != 1) {
            throw new BusinessException("商品已下架");
        }
        
        // 检查库存
        if (product.getStock() < cartDTO.getQuantity()) {
            throw new BusinessException("商品库存不足");
        }
        
        // 查询购物车是否已存在该商品
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, cartDTO.getProductId());
        Cart existCart = cartMapper.selectOne(wrapper);
        
        if (existCart != null) {
            // 更新数量
            existCart.setQuantity(existCart.getQuantity() + cartDTO.getQuantity());
            cartMapper.updateById(existCart);
        } else {
            // 新增购物车记录
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(cartDTO.getProductId());
            cart.setQuantity(cartDTO.getQuantity());
            cart.setChecked(cartDTO.getChecked());
            cartMapper.insert(cart);
        }
    }
    
    @Override
    @Transactional
    public void updateQuantity(Long userId, Long cartId, Integer quantity) {
        // 检查购物车记录是否存在
        Cart cart = getCartByUserIdAndCartId(userId, cartId);
        
        // 检查商品库存
        Product product = productService.getById(cart.getProductId());
        if (product.getStock() < quantity) {
            throw new BusinessException("商品库存不足");
        }
        
        // 更新数量
        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
    }
    
    @Override
    @Transactional
    public void updateChecked(Long userId, Long productId, Boolean checked) {
        Cart cart = getCartByUserIdAndCartId(userId, productId);
        cart.setChecked(checked);
        cartMapper.updateById(cart);
    }
    
    @Override
    @Transactional
    public void updateCheckedAll(Long userId, Boolean checked) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        
        Cart updateCart = new Cart();
        updateCart.setChecked(checked);
        
        cartMapper.update(updateCart, wrapper);
    }
    
    @Override
    @Transactional
    public void delete(Long userId, Long productId) {
        Cart cart = getCartByUserIdAndCartId(userId, productId);
        cartMapper.deleteById(cart.getId());
    }
    
    @Override
    @Transactional
    public void clear(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        cartMapper.delete(wrapper);
    }
    
    @Override
    public List<CartVO> list(Long userId) {
        // 查询用户购物车列表
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        List<Cart> cartList = cartMapper.selectList(wrapper);
        
        // 转换为VO
        List<CartVO> voList = new ArrayList<>();
        for (Cart cart : cartList) {
            Product product = productService.getById(cart.getProductId());
            if (product != null) {
                CartVO vo = new CartVO();
                vo.setId(cart.getId());
                vo.setProductId(product.getId());
                vo.setProductName(product.getName());
                vo.setProductImage(product.getMainImage());
                vo.setPrice(product.getPrice());
                vo.setQuantity(cart.getQuantity());
                vo.setChecked(cart.getChecked());
                vo.setTotalAmount(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
                voList.add(vo);
            }
        }
        return voList;
    }
    
    private Cart getCartByUserIdAndCartId(Long userId, Long cartId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getId, cartId);
        Cart cart = cartMapper.selectOne(wrapper);
        if (cart == null) {
            throw new BusinessException("购物车记录不存在");
        }
        return cart;
    }
} 