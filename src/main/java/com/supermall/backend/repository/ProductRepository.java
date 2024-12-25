package com.supermall.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductRepository extends BaseMapper<Product> {
    @Update("UPDATE product SET sales = sales + #{quantity} WHERE id = #{productId}")
    void increaseSales(@Param("productId") Long productId, @Param("quantity") Integer quantity);
} 