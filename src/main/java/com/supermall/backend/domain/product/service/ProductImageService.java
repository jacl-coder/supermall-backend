package com.supermall.backend.domain.product.service;

import com.supermall.backend.domain.product.dto.ProductImageRequest;
import com.supermall.backend.domain.product.dto.ProductImageResponse;
import java.util.List;

public interface ProductImageService {
    ProductImageResponse addImage(Long productId, ProductImageRequest request);
    void deleteImage(Long productId, Long imageId);
    void updateImageSort(Long productId, Long imageId, Integer sortOrder);
    List<ProductImageResponse> getProductImages(Long productId);
    void batchAddImages(Long productId, List<ProductImageRequest> requests);
    void batchDeleteImages(Long productId, List<Long> imageIds);
} 