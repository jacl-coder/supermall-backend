package com.supermall.backend.domain.product.service;

import com.supermall.backend.domain.product.dto.ProductSpecRequest;
import com.supermall.backend.domain.product.dto.ProductSpecResponse;
import java.util.List;

public interface ProductSpecService {
    ProductSpecResponse addSpec(Long productId, ProductSpecRequest request);
    void deleteSpec(Long productId, Long specId);
    void updateSpec(Long productId, Long specId, ProductSpecRequest request);
    List<ProductSpecResponse> getProductSpecs(Long productId);
    void batchAddSpecs(Long productId, List<ProductSpecRequest> requests);
    void batchDeleteSpecs(Long productId, List<Long> specIds);
} 