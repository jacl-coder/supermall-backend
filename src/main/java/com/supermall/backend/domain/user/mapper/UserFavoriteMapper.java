package com.supermall.backend.domain.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.user.dto.FavoriteResponse;
import com.supermall.backend.domain.user.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
    Page<FavoriteResponse> selectFavoritesByUserId(Page<FavoriteResponse> page, @Param("userId") Long userId);
} 