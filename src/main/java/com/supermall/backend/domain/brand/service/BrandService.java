package com.supermall.backend.domain.brand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.brand.entity.Brand;
import java.util.List;

public interface BrandService extends IService<Brand> {
    
    /**
     * 分页获取品牌列表
     */
    Page<Brand> listPage(Integer pageNum, Integer pageSize);
    
    /**
     * 获取所有品牌
     */
    List<Brand> listAll();
    
    /**
     * 更新品牌状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 批量删除品牌
     */
    void deleteBatch(List<Long> ids);
} 