package com.supermall.backend.domain.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.domain.review.entity.ProductReview;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductReviewMapper extends BaseMapper<ProductReview> {
} 