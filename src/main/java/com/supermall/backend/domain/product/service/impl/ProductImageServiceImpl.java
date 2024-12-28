package com.supermall.backend.domain.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.product.dto.ProductImageRequest;
import com.supermall.backend.domain.product.dto.ProductImageResponse;
import com.supermall.backend.domain.product.entity.ProductImage;
import com.supermall.backend.domain.product.mapper.ProductImageMapper;
import com.supermall.backend.domain.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl extends ServiceImpl<ProductImageMapper, ProductImage> implements ProductImageService {

    @Override
    @Transactional
    public ProductImageResponse addImage(Integer productId, ProductImageRequest request) {
        ProductImage image = new ProductImage();
        image.setProductId(productId);
        image.setImageUrl(request.getImageUrl());
        image.setSortOrder(request.getSortOrder());

        save(image);
        return convertToResponse(image);
    }

    @Override
    @Transactional
    public void deleteImage(Integer productId, Integer imageId) {
        remove(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId)
                .eq(ProductImage::getId, imageId));
    }

    @Override
    @Transactional
    public void updateImageSort(Integer productId, Integer imageId, Integer sortOrder) {
        ProductImage image = getOne(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId)
                .eq(ProductImage::getId, imageId));

        if (image == null) {
            throw new BusinessException("图片不存在");
        }

        image.setSortOrder(sortOrder);
        updateById(image);
    }

    @Override
    public List<ProductImageResponse> getProductImages(Integer productId) {
        List<ProductImage> images = list(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId)
                .orderByAsc(ProductImage::getSortOrder));

        return images.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void batchAddImages(Integer productId, List<ProductImageRequest> requests) {
        List<ProductImage> images = new ArrayList<>();
        for (ProductImageRequest request : requests) {
            ProductImage image = new ProductImage();
            image.setProductId(productId);
            image.setImageUrl(request.getImageUrl());
            image.setSortOrder(request.getSortOrder());
            images.add(image);
        }
        saveBatch(images);
    }

    @Override
    @Transactional
    public void batchDeleteImages(Integer productId, List<Integer> imageIds) {
        remove(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId)
                .in(ProductImage::getId, imageIds));
    }

    private ProductImageResponse convertToResponse(ProductImage image) {
        ProductImageResponse response = new ProductImageResponse();
        BeanUtils.copyProperties(image, response);
        return response;
    }
} 