package com.supermall.backend.domain.brand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.brand.entity.Brand;
import com.supermall.backend.domain.brand.vo.BrandVO;
import java.util.List;

public interface BrandService extends IService<Brand> {
    /**
     * 分页查询品牌
     */
    Page<BrandVO> page(Integer pageNum, Integer pageSize, String keyword);
    
    /**
     * 更新品牌状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 批量删除品牌
     */
    void deleteBatch(List<Long> ids);
    
    /**
     * 获取所有启用的品牌
     */
    List<BrandVO> listAll();
} 