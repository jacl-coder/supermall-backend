package com.supermall.backend.domain.brand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.domain.brand.entity.Brand;
import com.supermall.backend.domain.brand.mapper.BrandMapper;
import com.supermall.backend.domain.brand.service.BrandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    @Override
    @Transactional
    public void create(String name, String logo, String description, Integer sort) {
        Brand brand = new Brand();
        brand.setName(name);
        brand.setLogo(logo);
        brand.setDescription(description);
        brand.setSort(sort);
        brand.setStatus(1);
        save(brand);
    }

    @Override
    @Transactional
    public void update(Long id, String name, String logo, String description, Integer sort) {
        Brand brand = getById(id);
        if (brand == null) {
            throw new RuntimeException("品牌不存在");
        }
        brand.setName(name);
        brand.setLogo(logo);
        brand.setDescription(description);
        brand.setSort(sort);
        updateById(brand);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Brand brand = getById(id);
        if (brand == null) {
            throw new RuntimeException("品牌不存在");
        }
        brand.setStatus(status);
        updateById(brand);
    }

    @Override
    public Page<Brand> page(Integer pageNum, Integer pageSize) {
        return page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<Brand>()
                .orderByAsc(Brand::getSort));
    }

    @Override
    public Page<Brand> pageEnabled(Integer pageNum, Integer pageSize) {
        return page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<Brand>()
                .eq(Brand::getStatus, 1)
                .orderByAsc(Brand::getSort));
    }
} 