package com.supermall.backend.domain.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.product.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService extends IService<Product> {
    /**
     * 创建商品
     */
    void create(String name, String subtitle, Long categoryId, Long brandId,
                String mainImage, String subImages, String detail,
                BigDecimal price, Integer stock);

    /**
     * 更新商品
     */
    void update(Long id, String name, String subtitle,
                String mainImage, String subImages, String detail,
                BigDecimal price, Integer stock);

    /**
     * 删除商品
     */
    void delete(Long id);

    /**
     * 更新商品状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 分页获取商品列表
     */
    Page<Product> page(Integer pageNum, Integer pageSize);

    /**
     * 根据分类获取商品列表
     */
    List<Product> listByCategory(Long categoryId);

    /**
     * 根据品牌获取商品列表
     */
    List<Product> listByBrand(Long brandId);

    /**
     * 搜索商品
     */
    List<Product> search(String keyword);
} 