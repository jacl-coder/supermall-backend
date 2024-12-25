package com.supermall.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.entity.OrderLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderLogRepository extends BaseMapper<OrderLog> {
} 