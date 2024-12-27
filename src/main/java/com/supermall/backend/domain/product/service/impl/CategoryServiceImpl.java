package com.supermall.backend.domain.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.domain.product.entity.Category;
import com.supermall.backend.domain.product.mapper.CategoryMapper;
import com.supermall.backend.domain.product.service.CategoryService;
import com.supermall.backend.domain.product.dto.CategoryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    @Transactional
    public Category createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setParentId(request.getParentId());
        category.setLevel(request.getLevel());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setStatus("active");
        
        save(category);
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = getById(id);
        if (category != null) {
            category.setName(request.getName());
            category.setParentId(request.getParentId());
            category.setLevel(request.getLevel());
            category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : category.getSortOrder());
            
            updateById(category);
        }
        return category;
    }

    @Override
    public List<Category> getSubCategories(Long parentId) {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, parentId)
                .eq(Category::getStatus, "active")
                .orderByAsc(Category::getSortOrder));
    }

    @Override
    public List<Category> getAllCategories() {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, "active")
                .orderByAsc(Category::getLevel, Category::getSortOrder));
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long id) {
        // 检查是否有子分类
        long subCount = count(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, id)
                .eq(Category::getStatus, "active"));
                
        if (subCount > 0) {
            return false;
        }
        
        Category category = getById(id);
        if (category != null) {
            category.setStatus("inactive");
            return updateById(category);
        }
        return false;
    }
} 