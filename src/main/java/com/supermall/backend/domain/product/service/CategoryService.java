package com.supermall.backend.domain.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.product.entity.Category;
import com.supermall.backend.domain.product.dto.CategoryRequest;
import java.util.List;

public interface CategoryService extends IService<Category> {
    Category createCategory(CategoryRequest request);
    Category updateCategory(Long id, CategoryRequest request);
    List<Category> getSubCategories(Long parentId);
    List<Category> getAllCategories();
    boolean deleteCategory(Long id);
} 