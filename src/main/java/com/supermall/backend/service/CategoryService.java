package com.supermall.backend.service;

import com.supermall.backend.entity.Category;
import com.supermall.backend.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    void createCategory(Category category);
    void updateCategory(Category category);
    void deleteCategory(Long id);
    Category getCategoryById(Long id);
    List<Category> getCategoriesByParentId(Long parentId);
    List<Category> getAllCategories();
    List<CategoryVO> getCategoryTree();
}