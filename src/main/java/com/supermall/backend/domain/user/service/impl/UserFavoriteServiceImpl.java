package com.supermall.backend.domain.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.service.ProductService;
import com.supermall.backend.domain.user.dto.FavoriteResponse;
import com.supermall.backend.domain.user.entity.UserFavorite;
import com.supermall.backend.domain.user.mapper.UserFavoriteMapper;
import com.supermall.backend.domain.user.service.UserFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {

    private final ProductService productService;

    @Override
    @Transactional
    public void addFavorite(Integer userId, Integer productId) {
        // 检查商品是否存在
        Product product = productService.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查是否已经收藏
        boolean exists = exists(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getProductId, productId));
        if (exists) {
            throw new BusinessException("已经收藏过该商品");
        }

        // 添加收藏
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Integer userId, Integer productId) {
        remove(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getProductId, productId));
    }

    @Override
    public Page<FavoriteResponse> getFavorites(Integer userId, int page, int size) {
        // 分页查询收藏记录
        Page<UserFavorite> favoritePage = page(new Page<>(page, size),
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .orderByDesc(UserFavorite::getCreatedAt));

        // 转换为响应对象
        Page<FavoriteResponse> responsePage = new Page<>();
        responsePage.setTotal(favoritePage.getTotal());
        responsePage.setCurrent(favoritePage.getCurrent());
        responsePage.setSize(favoritePage.getSize());

        if (favoritePage.getRecords().isEmpty()) {
            return responsePage;
        }

        // 获取商品信息
        List<Product> products = productService.listByIds(
                favoritePage.getRecords().stream()
                        .map(UserFavorite::getProductId)
                        .collect(Collectors.toList()));
        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 组装响应数据
        List<FavoriteResponse> responses = favoritePage.getRecords().stream()
                .map(favorite -> {
                    Product product = productMap.get(favorite.getProductId());
                    if (product == null) {
                        return null;
                    }

                    FavoriteResponse response = new FavoriteResponse();
                    response.setId(favorite.getId());
                    response.setProductId(product.getId());
                    response.setProductName(product.getName());
                    response.setProductImage(product.getMainImage());
                    response.setPrice(product.getPrice());
                    response.setCreatedAt(favorite.getCreatedAt());
                    return response;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        responsePage.setRecords(responses);
        return responsePage;
    }

    @Override
    public boolean isFavorite(Integer userId, Integer productId) {
        return exists(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getProductId, productId));
    }
} 