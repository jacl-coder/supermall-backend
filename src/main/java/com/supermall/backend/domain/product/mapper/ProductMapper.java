package com.supermall.backend.domain.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.domain.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    int createProduct(Product product);

    Product getProduct(@Param("id") Integer id);

    List<Product> getProductsByCategory(
        @Param("categoryId") Integer categoryId,
        @Param("onSaleStatus") Product.Status onSaleStatus,
        @Param("offset") int offset,
        @Param("size") int size
    );

    long countProductsByCategory(
        @Param("categoryId") Integer categoryId,
        @Param("onSaleStatus") Product.Status onSaleStatus
    );

    List<Product> searchProducts(
        @Param("keyword") String keyword,
        @Param("onSaleStatus") Product.Status onSaleStatus,
        @Param("offset") int offset,
        @Param("size") int size
    );

    long countSearchProducts(
        @Param("keyword") String keyword,
        @Param("onSaleStatus") Product.Status onSaleStatus
    );
} 