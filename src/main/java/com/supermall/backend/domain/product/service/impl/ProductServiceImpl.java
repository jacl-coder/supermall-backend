package com.supermall.backend.domain.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.mapper.ProductMapper;
import com.supermall.backend.domain.product.service.ProductService;
import com.supermall.backend.domain.product.dto.ProductRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    @Transactional
    public Product createProduct(ProductRequest request, Long merchantId) {
        Product product = new Product();
        product.setMerchantId(merchantId);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategoryId(request.getCategoryId());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStock(request.getStock());
        product.setMainImage(request.getMainImage());
        product.setStatus("pending");
        
        save(product);
        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        Product product = getById(id);
        if (product != null) {
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setCategoryId(request.getCategoryId());
            product.setPrice(request.getPrice());
            product.setOriginalPrice(request.getOriginalPrice());
            product.setStock(request.getStock());
            product.setMainImage(request.getMainImage());
            
            updateById(product);
        }
        return product;
    }

    @Override
    public Page<Product> getProductsByCategory(Long categoryId, int page, int size) {
        return page(new Page<>(page, size), new LambdaQueryWrapper<Product>()
                .eq(Product::getCategoryId, categoryId)
                .eq(Product::getStatus, "on_sale")
                .orderByDesc(Product::getCreatedAt));
    }

    @Override
    public Page<Product> searchProducts(String keyword, int page, int size) {
        return page(new Page<>(page, size), new LambdaQueryWrapper<Product>()
                .like(Product::getName, keyword)
                .or()
                .like(Product::getDescription, keyword)
                .eq(Product::getStatus, "on_sale")
                .orderByDesc(Product::getCreatedAt));
    }
} 