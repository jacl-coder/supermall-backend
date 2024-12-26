package com.supermall.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductRepository extends BaseMapper<Product> {
    
    /**
     * 增加商品销量
     *
     * @param productId 商品ID
     * @param quantity 增加的数量
     * @return 更新的记录数
     */
    @Update("UPDATE product SET sales = sales + #{quantity} WHERE id = #{productId} AND deleted = 0")
    int increaseSales(@Param("productId") Long productId, @Param("quantity") Integer quantity);
} 