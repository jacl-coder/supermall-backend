package com.supermall.backend.service;

import com.supermall.backend.dto.CategoryDTO;
import com.supermall.backend.entity.Category;
import com.supermall.backend.vo.CategoryVO;
import java.util.List;

public interface CategoryService {
    // 创建分类
    Category createCategory(CategoryDTO categoryDTO);
    
    // 更新分类
    Category updateCategory(Long id, CategoryDTO categoryDTO);
    
    // 删除分类
    void deleteCategory(Long id);
    
    // 获取分类详情
    Category getCategory(Long id);
    
    // 获取分类树
    List<CategoryVO> getCategoryTree();
    
    // 获取所有分类
    List<Category> getAllCategories();
}