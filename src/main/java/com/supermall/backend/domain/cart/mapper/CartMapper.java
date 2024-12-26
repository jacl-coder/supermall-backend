package com.supermall.backend.domain.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.domain.cart.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
} 