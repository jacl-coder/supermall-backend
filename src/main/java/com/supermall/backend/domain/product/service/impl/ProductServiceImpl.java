package com.supermall.backend.domain.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.mapper.ProductMapper;
import com.supermall.backend.domain.product.service.ProductService;
import com.supermall.backend.domain.product.dto.ProductRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    
    @Override
    public Product getProduct(Integer id) {
        return baseMapper.getProduct(id);
    }
    
    @Override
    @Transactional
    public Product createProduct(ProductRequest request, Integer merchantId) {
        Product product = new Product();
        product.setMerchantId(merchantId);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategoryId(request.getCategoryId());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStock(request.getStock());
        product.setMainImage(request.getMainImage());
        product.setStatus(Product.Status.PENDING);
        product.setSales(0);
        
        baseMapper.createProduct(product);
        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Integer id, ProductRequest request) {
        Product product = getById(id);
        if (product != null) {
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setCategoryId(request.getCategoryId());
            product.setPrice(request.getPrice());
            product.setOriginalPrice(request.getOriginalPrice());
            product.setStock(request.getStock());
            product.setMainImage(request.getMainImage());
            product.setStatus(request.getStatus());
            
            updateById(product);
        }
        return product;
    }

    @Override
    public Page<Product> getProductsByCategory(Integer categoryId, int page, int size) {
        int offset = (page - 1) * size;
        List<Product> products = baseMapper.getProductsByCategory(categoryId, Product.Status.ON_SALE, offset, size);
        long total = baseMapper.countProductsByCategory(categoryId, Product.Status.ON_SALE);
        
        Page<Product> resultPage = new Page<>(page, size);
        resultPage.setRecords(products);
        resultPage.setTotal(total);
        return resultPage;
    }

    @Override
    public Page<Product> searchProducts(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        String likeKeyword = "%" + keyword + "%";
        
        List<Product> products = baseMapper.searchProducts(likeKeyword, Product.Status.ON_SALE, offset, size);
        long total = baseMapper.countSearchProducts(likeKeyword, Product.Status.ON_SALE);
        
        Page<Product> resultPage = new Page<>(page, size);
        resultPage.setRecords(products);
        resultPage.setTotal(total);
        return resultPage;
    }

    @Override
    @Transactional
    public Product updateProductStatus(Integer id, Product.Status status) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 验证状态转换的合法性
        validateStatusTransition(product.getStatus(), status);

        product.setStatus(status);
        updateById(product);
        return product;
    }

    private void validateStatusTransition(Product.Status currentStatus, Product.Status newStatus) {
        if (currentStatus == newStatus) {
            return;
        }

        switch (currentStatus) {
            case DRAFT -> {
                if (newStatus != Product.Status.PENDING) {
                    throw new BusinessException("草稿状态的商品只能提交审核");
                }
            }
            case PENDING -> {
                if (newStatus != Product.Status.APPROVED && newStatus != Product.Status.REJECTED) {
                    throw new BusinessException("待审核的商品只能审核通过或拒绝");
                }
            }
            case APPROVED -> {
                if (newStatus != Product.Status.ON_SALE) {
                    throw new BusinessException("已审核的商品只能上架");
                }
            }
            case ON_SALE -> {
                if (newStatus != Product.Status.OFF_SALE) {
                    throw new BusinessException("在售商品只能下架");
                }
            }
            case OFF_SALE -> {
                if (newStatus != Product.Status.ON_SALE && newStatus != Product.Status.PENDING) {
                    throw new BusinessException("下架商品只能重新上架或提交审核");
                }
            }
            case REJECTED -> {
                if (newStatus != Product.Status.PENDING) {
                    throw new BusinessException("被拒绝的商品需要重新提交审核");
                }
            }
            default -> throw new BusinessException("非法的商品状态");
        }
    }
} 