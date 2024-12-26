package com.supermall.backend.domain.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.mapper.ProductMapper;
import com.supermall.backend.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    @Transactional
    public Product create(String name, String subtitle, Long categoryId, Long brandId,
                         String mainImage, String subImages, String detail,
                         BigDecimal price, Integer stock) {
        Product product = new Product();
        product.setName(name);
        product.setSubtitle(subtitle);
        product.setCategoryId(categoryId);
        product.setBrandId(brandId);
        product.setMainImage(mainImage);
        product.setSubImages(subImages);
        product.setDetail(detail);
        product.setPrice(price);
        product.setStock(stock);
        product.setStatus(1); // 默认上架

        save(product);
        return product;
    }

    @Override
    @Transactional
    public void update(Long id, String name, String subtitle, String mainImage,
                      String subImages, String detail, BigDecimal price, Integer stock) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setName(name);
        product.setSubtitle(subtitle);
        product.setMainImage(mainImage);
        product.setSubImages(subImages);
        product.setDetail(detail);
        product.setPrice(price);
        product.setStock(stock);

        updateById(product);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        removeById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStatus(status);
        updateById(product);
    }

    @Override
    public Page<Product> page(Integer pageNum, Integer pageSize) {
        return page(new Page<>(pageNum, pageSize));
    }

    @Override
    public List<Product> listByCategory(Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getCategoryId, categoryId)
                .eq(Product::getStatus, 1);
        return list(wrapper);
    }

    @Override
    public List<Product> listByBrand(Long brandId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getBrandId, brandId)
                .eq(Product::getStatus, 1);
        return list(wrapper);
    }

    @Override
    public List<Product> search(String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Product::getName, keyword)
                .or()
                .like(Product::getSubtitle, keyword)
                .eq(Product::getStatus, 1);
        return list(wrapper);
    }
} 