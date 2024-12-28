package com.supermall.backend.domain.product.service;

import com.supermall.backend.domain.product.dto.ProductImageRequest;
import com.supermall.backend.domain.product.dto.ProductImageResponse;
import java.util.List;

public interface ProductImageService {
    ProductImageResponse addImage(Integer productId, ProductImageRequest request);
    void deleteImage(Integer productId, Integer imageId);
    void updateImageSort(Integer productId, Integer imageId, Integer sortOrder);
    List<ProductImageResponse> getProductImages(Integer productId);
    void batchAddImages(Integer productId, List<ProductImageRequest> requests);
    void batchDeleteImages(Integer productId, List<Integer> imageIds);
} 