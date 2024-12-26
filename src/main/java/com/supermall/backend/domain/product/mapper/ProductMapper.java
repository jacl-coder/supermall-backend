package com.supermall.backend.domain.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    /**
     * 分页查询商品（包含分类名称和品牌名称）
     */
    IPage<ProductVO> pageProducts(Page<Product> page, @Param("keyword") String keyword);
} 