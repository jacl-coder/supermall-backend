package com.supermall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.dto.CartDTO;
import com.supermall.backend.entity.CartItem;
import com.supermall.backend.entity.Product;
import com.supermall.backend.repository.CartItemRepository;
import com.supermall.backend.repository.ProductRepository;
import com.supermall.backend.service.CartService;
import com.supermall.backend.vo.CartItemVO;
import com.supermall.backend.vo.CartVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public CartVO addToCart(Long userId, CartDTO cartDTO) {
        // 检查商品是否存在
        Product product = productRepository.selectById(cartDTO.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查购物车是否已存在该商品
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                   .eq(CartItem::getProductId, cartDTO.getProductId());
        CartItem existingItem = cartItemRepository.selectOne(queryWrapper);

        if (existingItem != null) {
            // 更新数量
            existingItem.setQuantity(existingItem.getQuantity() + cartDTO.getQuantity());
            existingItem.setSelected(cartDTO.getSelected());
            cartItemRepository.updateById(existingItem);
        } else {
            // 新增购物车项
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(cartDTO.getProductId());
            cartItem.setQuantity(cartDTO.getQuantity());
            cartItem.setSelected(cartDTO.getSelected());
            cartItemRepository.insert(cartItem);
        }

        return getCart(userId);
    }

    @Override
    @Transactional
    public CartVO updateQuantity(Long userId, Long cartItemId, Integer quantity) {
        CartItem cartItem = getCartItem(userId, cartItemId);
        cartItem.setQuantity(quantity);
        cartItemRepository.updateById(cartItem);
        return getCart(userId);
    }

    @Override
    @Transactional
    public CartVO updateSelected(Long userId, Long cartItemId, Integer selected) {
        CartItem cartItem = getCartItem(userId, cartItemId);
        cartItem.setSelected(selected);
        cartItemRepository.updateById(cartItem);
        return getCart(userId);
    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        // 验证购物车项是否存在且属于该用户
        getCartItem(userId, cartItemId);
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public CartVO getCart(Long userId) {
        // 查询用户的购物车项
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                   .orderByDesc(CartItem::getCreateTime);
        
        List<CartItem> cartItems = cartItemRepository.selectList(queryWrapper);
        
        // 构建返回对象
        CartVO cartVO = new CartVO();
        List<CartItemVO> items = cartItems.stream()
                .map(item -> {
                    Product product = productRepository.selectById(item.getProductId());
                    return convertToVO(item, product);
                })
                .collect(Collectors.toList());
        
        cartVO.setItems(items);
        cartVO.setAllSelected(items.stream().allMatch(item -> item.getSelected() == 1));
        cartVO.setTotalPrice(calculateTotalPrice(items));
        
        return cartVO;
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId);
        cartItemRepository.delete(queryWrapper);
    }

    @Override
    @Transactional
    public CartVO updateAllSelected(Long userId, Integer selected) {
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId);
        
        CartItem updateItem = new CartItem();
        updateItem.setSelected(selected);
        
        cartItemRepository.update(updateItem, queryWrapper);
        return getCart(userId);
    }

    private CartItem getCartItem(Long userId, Long cartItemId) {
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getId, cartItemId)
                   .eq(CartItem::getUserId, userId);
        
        CartItem cartItem = cartItemRepository.selectOne(queryWrapper);
        if (cartItem == null) {
            throw new BusinessException("购物车商品不存在");
        }
        return cartItem;
    }

    private CartItemVO convertToVO(CartItem cartItem, Product product) {
        CartItemVO vo = new CartItemVO();
        BeanUtils.copyProperties(cartItem, vo);
        
        vo.setProductName(product.getName());
        vo.setProductImage(product.getMainImage());
        vo.setPrice(product.getPrice());
        vo.setTotalPrice(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        
        return vo;
    }

    private BigDecimal calculateTotalPrice(List<CartItemVO> items) {
        return items.stream()
                .filter(item -> item.getSelected() == 1)
                .map(CartItemVO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 