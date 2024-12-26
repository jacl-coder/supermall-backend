package com.supermall.backend.service;

import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProductSalesService {

    private final ProductRepository productRepository;

    public ProductSalesService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 增加商品销量
     *
     * @param productId 商品ID
     * @param quantity 增加的数量
     */
    @Transactional
    public void increaseSales(Long productId, Integer quantity) {
        // 检查参数
        if (productId == null || productId <= 0) {
            throw new BusinessException("商品ID不合法");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("数量不合法");
        }

        // 增加销量
        int rows = productRepository.increaseSales(productId, quantity);
        if (rows == 0) {
            log.error("Failed to increase sales for product {}, quantity: {}", productId, quantity);
            throw new BusinessException("更新商品销量失败");
        }
        
        log.info("Successfully increased sales for product {}, quantity: {}", productId, quantity);
    }
} 