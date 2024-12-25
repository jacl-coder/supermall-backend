package com.supermall.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.entity.OrderDelivery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDeliveryRepository extends BaseMapper<OrderDelivery> {
} 