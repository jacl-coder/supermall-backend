package com.supermall.backend.domain.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.domain.cart.entity.Cart;
import com.supermall.backend.domain.cart.vo.CartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
    /**
     * 获取用户购物车列表
     */
    List<CartVO> listByUserId(@Param("userId") Long userId);
    
    /**
     * 获取购物车项详情
     */
    CartVO getCartById(@Param("id") Long id);
    
    /**
     * 清空用户购物车
     */
    int clearByUserId(@Param("userId") Long userId);
} 