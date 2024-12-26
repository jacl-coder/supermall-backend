package com.supermall.backend.domain.category.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.category.entity.Category;
import com.supermall.backend.domain.category.vo.CategoryVO;
import java.util.List;

public interface CategoryService extends IService<Category> {
    /**
     * 获取分类列表
     */
    List<CategoryVO> listCategories();
    
    /**
     * 获取分类详情
     */
    CategoryVO getDetail(Long id);
    
    /**
     * 更新分类状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 获取子分类列表
     */
    List<Category> listChildren(Long parentId);
    
    /**
     * 构建分类树
     */
    List<CategoryVO> buildTree(List<CategoryVO> categories);
} 