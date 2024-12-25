package com.supermall.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemRepository extends BaseMapper<OrderItem> {
} 