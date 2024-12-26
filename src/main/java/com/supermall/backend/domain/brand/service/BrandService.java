package com.supermall.backend.domain.brand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.brand.entity.Brand;

public interface BrandService extends IService<Brand> {
    void create(String name, String logo, String description, Integer sort);

    void update(Long id, String name, String logo, String description, Integer sort);

    void delete(Long id);

    void updateStatus(Long id, Integer status);

    Page<Brand> page(Integer pageNum, Integer pageSize);

    Page<Brand> pageEnabled(Integer pageNum, Integer pageSize);
} 