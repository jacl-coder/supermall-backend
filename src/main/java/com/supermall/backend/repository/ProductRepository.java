package com.supermall.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductRepository extends BaseMapper<Product> {
} 