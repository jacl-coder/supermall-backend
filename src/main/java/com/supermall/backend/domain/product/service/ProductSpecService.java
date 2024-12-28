package com.supermall.backend.domain.product.service;

import com.supermall.backend.domain.product.dto.ProductSpecRequest;
import com.supermall.backend.domain.product.dto.ProductSpecResponse;
import java.util.List;

public interface ProductSpecService {
    ProductSpecResponse addSpec(Integer productId, ProductSpecRequest request);
    void deleteSpec(Integer productId, Integer specId);
    void updateSpec(Integer productId, Integer specId, ProductSpecRequest request);
    List<ProductSpecResponse> getProductSpecs(Integer productId);
    void batchAddSpecs(Integer productId, List<ProductSpecRequest> requests);
    void batchDeleteSpecs(Integer productId, List<Integer> specIds);
} 