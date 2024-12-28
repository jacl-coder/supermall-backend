package com.supermall.backend.domain.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.dto.ProductRequest;

public interface ProductService extends IService<Product> {
    Product getProduct(Integer id);
    
    Product createProduct(ProductRequest request, Integer merchantId);
    
    Product updateProduct(Integer id, ProductRequest request);
    
    Product updateProductStatus(Integer id, Product.Status status);
    
    Page<Product> getProductsByCategory(Integer categoryId, int page, int size);
    
    Page<Product> searchProducts(String keyword, int page, int size);

    Page<Product> getProductsByMerchant(Integer merchantId, int page, int size);
    
    Page<Product> getProducts(int page, int size);
} 