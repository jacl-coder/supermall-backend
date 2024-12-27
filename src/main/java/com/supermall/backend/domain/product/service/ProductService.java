package com.supermall.backend.domain.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.dto.ProductRequest;

public interface ProductService extends IService<Product> {
    Product createProduct(ProductRequest request, Long merchantId);
    Product updateProduct(Long id, ProductRequest request);
    Page<Product> getProductsByCategory(Long categoryId, int page, int size);
    Page<Product> searchProducts(String keyword, int page, int size);
} 