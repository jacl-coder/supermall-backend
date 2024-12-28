package com.supermall.backend.domain.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartService {

    private final ProductService productService;

    @Override
    @Transactional
    public CartItemResponse addToCart(Integer userId, CartItemRequest request) {
        // 检查商品是否存在且在售
        Product product = productService.getById(request.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (product.getStatus() != Product.Status.ON_SALE) {
            throw new BusinessException("商品已下架");
        }
        if (product.getStock() < request.getQuantity()) {
            throw new BusinessException("商品库存不足");
        }

        // 检查是否已存在
        CartItem existingItem = getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, request.getProductId()));

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (product.getStock() < newQuantity) {
                throw new BusinessException("商品库存不足");
            }
            existingItem.setQuantity(newQuantity);
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
    public CartItemResponse updateQuantity(Integer userId, Integer itemId, Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessException("商品数量必须大于0");
        }

        CartItem cartItem = getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, itemId)
                .eq(CartItem::getUserId, userId));
                
        if (cartItem == null) {
            throw new BusinessException("购物车商品不存在");
        }

        // 检查商品库存
        Product product = productService.getById(cartItem.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (product.getStatus() != Product.Status.ON_SALE) {
            throw new BusinessException("商品已下架");
        }
        if (product.getStock() < quantity) {
            throw new BusinessException("商品库存不足");
        }

        cartItem.setQuantity(quantity);
        updateById(cartItem);
        return convertToResponse(cartItem);
    }

    @Override
    @Transactional
    public void removeFromCart(Integer userId, Integer itemId) {
        CartItem cartItem = getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, itemId)
                .eq(CartItem::getUserId, userId));
                
        if (cartItem == null) {
            throw new BusinessException("购物车商品不存在");
        }

        removeById(itemId);
    }

    @Override
    @Transactional
    public void updateSelected(Integer userId, Integer itemId, Boolean selected) {
        CartItem cartItem = getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, itemId)
                .eq(CartItem::getUserId, userId));
                
        if (cartItem == null) {
            throw new BusinessException("购物车商品不存在");
        }

        cartItem.setSelected(selected);
        updateById(cartItem);
    }

    @Override
    @Transactional
    public void updateAllSelected(Integer userId, Boolean selected) {
        List<CartItem> items = list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId));
                
        if (!items.isEmpty()) {
            items.forEach(item -> item.setSelected(selected));
            updateBatchById(items);
        }
    }

    @Override
    public List<CartItemResponse> getUserCart(Integer userId) {
        List<CartItem> items = list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId));
                
        if (items.isEmpty()) {
            return List.of();
        }

        // 批量获取商品信息
        List<Integer> productIds = items.stream()
                .map(CartItem::getProductId)
                .collect(Collectors.toList());
        List<Product> products = productService.listByIds(productIds);
        var productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        return items.stream()
                .map(item -> convertToResponse(item, productMap.get(item.getProductId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void clearCart(Integer userId) {
        remove(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId));
    }

    @Override
    public List<CartItem> getByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return list(new LambdaQueryWrapper<CartItem>()
                .in(CartItem::getId, ids));
    }

    @Override
    @Transactional
    public void deleteByIds(List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            removeBatchByIds(ids);
        }
    }

    @Override
    @Transactional
    public void batchRemoveFromCart(Integer userId, List<Integer> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return;
        }

        // 验证所有要删除的商品是否属于当前用户
        List<CartItem> items = list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .in(CartItem::getId, itemIds));

        if (items.size() != itemIds.size()) {
            throw new BusinessException("部分商品不存在或无权操作");
        }

        removeBatchByIds(itemIds);
    }

    @Override
    @Transactional
    public void batchUpdateSelected(Integer userId, List<Integer> itemIds, Boolean selected) {
        if (itemIds == null || itemIds.isEmpty()) {
            return;
        }

        // 验证所有要更新的商品是否属于当前用户
        List<CartItem> items = list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .in(CartItem::getId, itemIds));

        if (items.size() != itemIds.size()) {
            throw new BusinessException("部分商品不存在或无权操作");
        }

        items.forEach(item -> item.setSelected(selected));
        updateBatchById(items);
    }

    @Override
    @Transactional
    public void batchUpdateQuantity(Integer userId, List<Integer> itemIds, List<Integer> quantities) {
        if (itemIds == null || itemIds.isEmpty() || quantities == null || quantities.isEmpty() 
                || itemIds.size() != quantities.size()) {
            throw new BusinessException("参数错误");
        }

        // 验证所有要更新的商品是否属于当前用户
        List<CartItem> items = list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .in(CartItem::getId, itemIds));

        if (items.size() != itemIds.size()) {
            throw new BusinessException("部分商品不存在或无权操作");
        }

        // 获取所有相关商品的信息
        List<Integer> productIds = items.stream()
                .map(CartItem::getProductId)
                .collect(Collectors.toList());
        List<Product> products = productService.listByIds(productIds);
        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 更新数量并验证库存
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            Integer quantity = quantities.get(i);
            
            if (quantity <= 0) {
                throw new BusinessException("商品数量必须大于0");
            }

            Product product = productMap.get(item.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在");
            }
            if (product.getStatus() != Product.Status.ON_SALE) {
                throw new BusinessException("商品已下架: " + product.getName());
            }
            if (product.getStock() < quantity) {
                throw new BusinessException("商品库存不足: " + product.getName());
            }

            item.setQuantity(quantity);
        }

        updateBatchById(items);
    }

    private CartItemResponse convertToResponse(CartItem item) {
        Product product = productService.getById(item.getProductId());
        return convertToResponse(item, product);
    }

    private CartItemResponse convertToResponse(CartItem item, Product product) {
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setProductName(product.getName());
        response.setProductImage(product.getMainImage());
        response.setPrice(product.getPrice());
        response.setQuantity(item.getQuantity());
        response.setSelected(item.getSelected());
        response.setTotalPrice(product.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())));
        response.setStock(product.getStock());
        response.setStatus(product.getStatus());
        return response;
    }
} 