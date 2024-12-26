package com.supermall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.entity.Category;
import com.supermall.backend.repository.CategoryRepository;
import com.supermall.backend.service.CategoryService;
import com.supermall.backend.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void createCategory(Category category) {
        // 检查同级分类名称是否重复
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, category.getParentId())
                .eq(Category::getName, category.getName())
                .eq(Category::getDeleted, 0);
        
        if (categoryRepository.selectCount(queryWrapper) > 0) {
            throw new BusinessException("同级分类名称已存在");
        }

        // 如果是子分类，检查父分类是否存在
        if (category.getParentId() != 0) {
            Category parent = categoryRepository.selectById(category.getParentId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
            category.setLevel(parent.getLevel() + 1);
        } else {
            category.setLevel(1);
        }

        categoryRepository.insert(category);
    }

    @Override
    @Transactional
    public void updateCategory(Category category) {
        // 检查分类是否存在
        Category existingCategory = categoryRepository.selectById(category.getId());
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查同级分类名称是否重复
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, category.getParentId())
                .eq(Category::getName, category.getName())
                .ne(Category::getId, category.getId())
                .eq(Category::getDeleted, 0);
        
        if (categoryRepository.selectCount(queryWrapper) > 0) {
            throw new BusinessException("同级分类名称已存在");
        }

        categoryRepository.updateById(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        // 检查分类是否存在
        Category category = categoryRepository.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查是否有子分类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, id)
                .eq(Category::getDeleted, 0);
        
        if (categoryRepository.selectCount(queryWrapper) > 0) {
            throw new BusinessException("请先删除子分类");
        }

        categoryRepository.deleteById(id);
    }

    @Override
    public Category getCategoryById(Long id) {
        Category category = categoryRepository.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    @Override
    public List<Category> getCategoriesByParentId(Long parentId) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, parentId)
                .eq(Category::getDeleted, 0)
                .orderByAsc(Category::getSort);
        return categoryRepository.selectList(queryWrapper);
    }

    @Override
    public List<Category> getAllCategories() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getDeleted, 0)
                .orderByAsc(Category::getSort);
        return categoryRepository.selectList(queryWrapper);
    }

    @Override
    public List<CategoryVO> getCategoryTree() {
        // 获取所有分类
        List<Category> allCategories = getAllCategories();
        
        // 转换为CategoryVO
        List<CategoryVO> categoryVOs = allCategories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建父子关系
        Map<Long, List<CategoryVO>> childrenMap = categoryVOs.stream()
                .filter(vo -> vo.getParentId() != 0)
                .collect(Collectors.groupingBy(CategoryVO::getParentId));

        // 设置子分类
        categoryVOs.forEach(vo -> vo.setChildren(childrenMap.getOrDefault(vo.getId(), new ArrayList<>())));

        // 返回顶级分类
        return categoryVOs.stream()
                .filter(vo -> vo.getParentId() == 0)
                .collect(Collectors.toList());
    }

    private CategoryVO convertToVO(Category category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        vo.setChildren(new ArrayList<>());
        return vo;
    }
} 