package com.supermall.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemRepository extends BaseMapper<CartItem> {
} 