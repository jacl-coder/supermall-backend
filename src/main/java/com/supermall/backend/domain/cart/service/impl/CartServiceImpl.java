package com.supermall.backend.domain.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.cart.entity.Cart;
import com.supermall.backend.domain.cart.mapper.CartMapper;
import com.supermall.backend.domain.cart.service.CartService;
import com.supermall.backend.domain.cart.vo.CartVO;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
    
    private final ProductService productService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartVO addToCart(Long userId, Long productId, Integer quantity) {
        log.debug("添加商品到购物车: userId={}, productId={}, quantity={}", userId, productId, quantity);
        try {
            // 检查商品是否存在
            Product product = productService.getById(productId);
            if (product == null) {
                throw new ApiException("商品不存在");
            }
            
            // 检查商品是否上架
            if (product.getStatus() != 1) {
                throw new ApiException("商品已下架");
            }
            
            // 检查库存
            if (product.getStock() < quantity) {
                throw new ApiException("商品库存不足");
            }
            
            // 检查是否已在购物车中
            Cart existingCart = getOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId)
                .eq(Cart::getDeleted, 0));
            
            if (existingCart != null) {
                // 更新数量
                existingCart.setQuantity(existingCart.getQuantity() + quantity);
                updateById(existingCart);
                return baseMapper.getCartById(existingCart.getId());
            }
            
            // 创建新购物车项
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setChecked(true);
            save(cart);
            
            return baseMapper.getCartById(cart.getId());
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("添加商品到购物车失败", e);
            throw new ApiException("添加商品到购物车失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<CartVO> listUserCart(Long userId) {
        log.debug("获取用户购物车列表: userId={}", userId);
        try {
            return baseMapper.listByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户购物车列表失败", e);
            throw new ApiException("获取用户购物车列表失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(Long id, Integer quantity) {
        log.debug("更新购物车商品数量: id={}, quantity={}", id, quantity);
        try {
            Cart cart = getById(id);
            if (cart == null) {
                throw new ApiException("购物车商品不存在");
            }
            
            // 检查库存
            Product product = productService.getById(cart.getProductId());
            if (product.getStock() < quantity) {
                throw new ApiException("商品库存不足");
            }
            
            cart.setQuantity(quantity);
            updateById(cart);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新购物车商品数量失败", e);
            throw new ApiException("更新购物车商品数量失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChecked(Long id, Boolean checked) {
        log.debug("更新购物车商品选中状态: id={}, checked={}", id, checked);
        try {
            Cart cart = getById(id);
            if (cart == null) {
                throw new ApiException("购物车商品不存在");
            }
            
            cart.setChecked(checked);
            updateById(cart);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新购物车商品选中状态失败", e);
            throw new ApiException("更新购物车商品选中状态失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCartItem(Long id) {
        log.debug("删除购物车商品: id={}", id);
        try {
            Cart cart = getById(id);
            if (cart == null) {
                throw new ApiException("购物车商品不存在");
            }
            
            removeById(id);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除购物车商品失败", e);
            throw new ApiException("删除购物车商品失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userId) {
        log.debug("清空用户购物车: userId={}", userId);
        try {
            baseMapper.clearByUserId(userId);
        } catch (Exception e) {
            log.error("清空用户购物车失败", e);
            throw new ApiException("清空用户购物车失败: " + e.getMessage());
        }
    }
} 