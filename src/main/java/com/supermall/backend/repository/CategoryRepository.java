package com.supermall.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryRepository extends BaseMapper<Category> {
} 