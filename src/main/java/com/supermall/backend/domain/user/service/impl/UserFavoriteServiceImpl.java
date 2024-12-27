package com.supermall.backend.domain.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.mapper.ProductMapper;
import com.supermall.backend.domain.user.entity.UserFavorite;
import com.supermall.backend.domain.user.mapper.UserFavoriteMapper;
import com.supermall.backend.domain.user.dto.UserFavoriteResponse;
import com.supermall.backend.domain.user.service.UserFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {

    private final ProductMapper productMapper;

    @Override
    @Transactional
    public UserFavoriteResponse addFavorite(Long userId, Long productId) {
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 检查是否已收藏
        if (isFavorite(userId, productId)) {
            throw new BusinessException("已收藏该商品");
        }
        
        // 创建收藏记录
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        
        save(favorite);
        
        // 转换为响应对象
        UserFavoriteResponse response = new UserFavoriteResponse();
        BeanUtils.copyProperties(favorite, response);
        BeanUtils.copyProperties(product, response);
        
        return response;
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long productId) {
        remove(new LambdaQueryWrapper<UserFavorite>()
            .eq(UserFavorite::getUserId, userId)
            .eq(UserFavorite::getProductId, productId));
    }

    @Override
    public Page<UserFavoriteResponse> getUserFavorites(Long userId, int page, int size) {
        // 获取收藏记录
        Page<UserFavorite> favoritePage = page(
            new Page<>(page, size),
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .orderByDesc(UserFavorite::getCreatedAt)
        );
        
        // 转换为响应对象
        Page<UserFavoriteResponse> responsePage = new Page<>(
            favoritePage.getCurrent(),
            favoritePage.getSize(),
            favoritePage.getTotal()
        );
        
        responsePage.setRecords(favoritePage.getRecords().stream()
            .map(favorite -> {
                UserFavoriteResponse response = new UserFavoriteResponse();
                BeanUtils.copyProperties(favorite, response);
                
                // 获取商品信息
                Product product = productMapper.selectById(favorite.getProductId());
                if (product != null) {
                    BeanUtils.copyProperties(product, response);
                }
                
                return response;
            })
            .toList());
        
        return responsePage;
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        return count(new LambdaQueryWrapper<UserFavorite>()
            .eq(UserFavorite::getUserId, userId)
            .eq(UserFavorite::getProductId, productId)) > 0;
    }

    @Override
    public Long getFavoriteCount(Long userId) {
        return count(new LambdaQueryWrapper<UserFavorite>()
            .eq(UserFavorite::getUserId, userId));
    }
} 