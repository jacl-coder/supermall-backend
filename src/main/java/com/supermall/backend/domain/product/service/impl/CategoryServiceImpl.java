package com.supermall.backend.domain.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
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
        // 验证父分类
        if (request.getParentId() != null && request.getParentId() != 0) {
            Category parent = getById(request.getParentId());
            if (parent == null || parent.getStatus() == Category.Status.INACTIVE) {
                throw new BusinessException("父分类不存在或已禁用");
            }
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setParentId(request.getParentId());
        category.setLevel(request.getLevel());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setStatus(Category.Status.ACTIVE);
        
        save(category);
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Integer id, CategoryRequest request) {
        Category category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 验证父分类
        if (request.getParentId() != null && request.getParentId() != 0) {
            if (request.getParentId().equals(id)) {
                throw new BusinessException("父分类不能是自己");
            }
            Category parent = getById(request.getParentId());
            if (parent == null || parent.getStatus() == Category.Status.INACTIVE) {
                throw new BusinessException("父分类不存在或已禁用");
            }
        }

        category.setName(request.getName());
        category.setParentId(request.getParentId());
        category.setLevel(request.getLevel());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : category.getSortOrder());
        
        updateById(category);
        return category;
    }

    @Override
    public List<Category> getSubCategories(Integer parentId) {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, parentId)
                .eq(Category::getStatus, Category.Status.ACTIVE)
                .orderByAsc(Category::getSortOrder));
    }

    @Override
    public List<Category> getAllCategories() {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, Category.Status.ACTIVE)
                .orderByAsc(Category::getLevel, Category::getSortOrder));
    }

    @Override
    @Transactional
    public boolean deleteCategory(Integer id) {
        Category category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查是否有子分类
        long subCount = count(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, id)
                .eq(Category::getStatus, Category.Status.ACTIVE));
                
        if (subCount > 0) {
            throw new BusinessException("存在子分类，无法删除");
        }
        
        category.setStatus(Category.Status.INACTIVE);
        return updateById(category);
    }
} 