package com.supermall.backend.service.impl;

import com.supermall.backend.dto.CategoryDTO;
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
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryRepository.insert(category);
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        BeanUtils.copyProperties(categoryDTO, category);
        categoryRepository.updateById(category);
        return category;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Category getCategory(Long id) {
        return categoryRepository.selectById(id);
    }

    @Override
    public List<CategoryVO> getCategoryTree() {
        List<Category> allCategories = categoryRepository.selectList(null);
        
        // 转换为 CategoryVO
        List<CategoryVO> categoryVOs = allCategories.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());

        // 构建树形结构
        Map<Long, List<CategoryVO>> childrenMap = categoryVOs.stream()
            .filter(vo -> vo.getParentId() != null)
            .collect(Collectors.groupingBy(CategoryVO::getParentId));

        // 设置子分类
        categoryVOs.forEach(vo -> vo.setChildren(childrenMap.getOrDefault(vo.getId(), new ArrayList<>())));

        // 返回顶级分类
        return categoryVOs.stream()
            .filter(vo -> vo.getParentId() == null)
            .collect(Collectors.toList());
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.selectList(null);
    }

    private CategoryVO convertToVO(Category category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
} 