package com.supermall.backend.domain.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.domain.cart.dto.CartItemRequest;
import com.supermall.backend.domain.cart.dto.CartItemResponse;
import com.supermall.backend.domain.cart.entity.CartItem;
import com.supermall.backend.domain.cart.mapper.CartItemMapper;
import com.supermall.backend.domain.cart.service.CartService;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartService {

    private final ProductService productService;

    @Override
    @Transactional
    public CartItemResponse addToCart(Long userId, CartItemRequest request) {
        // ��查是否已存在
        CartItem existingItem = getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, request.getProductId()));

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            updateById(existingItem);
            return convertToResponse(existingItem);
        }

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProductId(request.getProductId());
        cartItem.setQuantity(request.getQuantity());
        cartItem.setSelected(true);
        
        save(cartItem);
        return convertToResponse(cartItem);
    }

    @Override
    @Transactional
    public CartItemResponse updateQuantity(Long userId, Long itemId, Integer quantity) {
        CartItem cartItem = getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, itemId)
                .eq(CartItem::getUserId, userId));
                
        if (cartItem != null) {
            cartItem.setQuantity(quantity);
            updateById(cartItem);
            return convertToResponse(cartItem);
        }
        return null;
    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Long itemId) {
        remove(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, itemId)
                .eq(CartItem::getUserId, userId));
    }

    @Override
    @Transactional
    public void updateSelected(Long userId, Long itemId, Boolean selected) {
        CartItem cartItem = getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, itemId)
                .eq(CartItem::getUserId, userId));
                
        if (cartItem != null) {
            cartItem.setSelected(selected);
            updateById(cartItem);
        }
    }

    @Override
    @Transactional
    public void updateAllSelected(Long userId, Boolean selected) {
        List<CartItem> items = list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId));
                
        items.forEach(item -> item.setSelected(selected));
        updateBatchById(items);
    }

    @Override
    public List<CartItemResponse> getUserCart(Long userId) {
        List<CartItem> items = list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId));
                
        List<CartItemResponse> responses = new ArrayList<>();
        for (CartItem item : items) {
            responses.add(convertToResponse(item));
        }
        return responses;
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        remove(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId));
    }

    @Override
    public List<CartItem> getByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return list(new LambdaQueryWrapper<CartItem>()
                .in(CartItem::getId, ids));
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            removeBatchByIds(ids);
        }
    }

    private CartItemResponse convertToResponse(CartItem item) {
        Product product = productService.getById(item.getProductId());
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setProductName(product.getName());
        response.setProductImage(product.getMainImage());
        response.setPrice(product.getPrice());
        response.setQuantity(item.getQuantity());
        response.setSelected(item.getSelected());
        response.setTotalPrice(product.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())));
        return response;
    }
} 