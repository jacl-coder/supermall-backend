package com.supermall.backend.domain.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.product.dto.ProductSpecRequest;
import com.supermall.backend.domain.product.dto.ProductSpecResponse;
import com.supermall.backend.domain.product.entity.ProductSpec;
import com.supermall.backend.domain.product.mapper.ProductSpecMapper;
import com.supermall.backend.domain.product.service.ProductSpecService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper, ProductSpec> implements ProductSpecService {

    @Override
    @Transactional
    public ProductSpecResponse addSpec(Long productId, ProductSpecRequest request) {
        // 检查是否已存在相同规格名称
        if (exists(new LambdaQueryWrapper<ProductSpec>()
                .eq(ProductSpec::getProductId, productId)
                .eq(ProductSpec::getSpecName, request.getSpecName()))) {
            throw new BusinessException("该规格名称已存在");
        }

        ProductSpec spec = new ProductSpec();
        spec.setProductId(productId);
        spec.setSpecName(request.getSpecName());
        spec.setSpecValue(request.getSpecValue());

        save(spec);
        return convertToResponse(spec);
    }

    @Override
    @Transactional
    public void deleteSpec(Long productId, Long specId) {
        remove(new LambdaQueryWrapper<ProductSpec>()
                .eq(ProductSpec::getProductId, productId)
                .eq(ProductSpec::getId, specId));
    }

    @Override
    @Transactional
    public void updateSpec(Long productId, Long specId, ProductSpecRequest request) {
        ProductSpec spec = getOne(new LambdaQueryWrapper<ProductSpec>()
                .eq(ProductSpec::getProductId, productId)
                .eq(ProductSpec::getId, specId));

        if (spec == null) {
            throw new BusinessException("规格不存在");
        }

        // 检查是否与其他规格名称冲突
        if (!spec.getSpecName().equals(request.getSpecName()) &&
                exists(new LambdaQueryWrapper<ProductSpec>()
                        .eq(ProductSpec::getProductId, productId)
                        .eq(ProductSpec::getSpecName, request.getSpecName()))) {
            throw new BusinessException("该规格名称已存在");
        }

        spec.setSpecName(request.getSpecName());
        spec.setSpecValue(request.getSpecValue());
        updateById(spec);
    }

    @Override
    public List<ProductSpecResponse> getProductSpecs(Long productId) {
        List<ProductSpec> specs = list(new LambdaQueryWrapper<ProductSpec>()
                .eq(ProductSpec::getProductId, productId)
                .orderByAsc(ProductSpec::getCreatedAt));

        return specs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void batchAddSpecs(Long productId, List<ProductSpecRequest> requests) {
        // 检查是否有重复的规格名称
        List<String> specNames = requests.stream()
                .map(ProductSpecRequest::getSpecName)
                .collect(Collectors.toList());
        
        if (specNames.size() != specNames.stream().distinct().count()) {
            throw new BusinessException("存在重复的规格名称");
        }

        // 检查是否与已有规格名称冲突
        long existingCount = count(new LambdaQueryWrapper<ProductSpec>()
                .eq(ProductSpec::getProductId, productId)
                .in(ProductSpec::getSpecName, specNames));
        
        if (existingCount > 0) {
            throw new BusinessException("部分规格名称已存在");
        }

        List<ProductSpec> specs = new ArrayList<>();
        for (ProductSpecRequest request : requests) {
            ProductSpec spec = new ProductSpec();
            spec.setProductId(productId);
            spec.setSpecName(request.getSpecName());
            spec.setSpecValue(request.getSpecValue());
            specs.add(spec);
        }
        saveBatch(specs);
    }

    @Override
    @Transactional
    public void batchDeleteSpecs(Long productId, List<Long> specIds) {
        remove(new LambdaQueryWrapper<ProductSpec>()
                .eq(ProductSpec::getProductId, productId)
                .in(ProductSpec::getId, specIds));
    }

    private ProductSpecResponse convertToResponse(ProductSpec spec) {
        ProductSpecResponse response = new ProductSpecResponse();
        BeanUtils.copyProperties(spec, response);
        return response;
    }
} 