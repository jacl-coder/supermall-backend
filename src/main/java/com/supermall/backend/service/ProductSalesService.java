package com.supermall.backend.service;

import org.springframework.stereotype.Service;
import com.supermall.backend.repository.ProductRepository;

@Service
public class ProductSalesService {
    
    private final ProductRepository productRepository;

    public ProductSalesService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void increaseSales(Long productId, Integer quantity) {
        productRepository.increaseSales(productId, quantity);
    }
} 