package com.supermall.backend.domain.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.domain.category.entity.Category;
import com.supermall.backend.domain.category.mapper.CategoryMapper;
import com.supermall.backend.domain.category.service.CategoryService;
import com.supermall.backend.domain.category.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类服务实现类
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(String name, Long parentId, Integer sort, String icon) {
        // 检查父级分类是否存在
        Category parent = null;
        if (parentId != 0) {
            parent = categoryMapper.selectById(parentId);
            if (parent == null) {
                throw new RuntimeException("父级分类不存在");
            }
        }

        // 创建分类
        Category category = new Category();
        category.setName(name);
        category.setParentId(parentId);
        category.setSort(sort);
        category.setIcon(icon);
        category.setStatus(1);
        category.setLevel(parent == null ? 1 : parent.getLevel() + 1);

        categoryMapper.insert(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, String name, Integer sort, String icon) {
        // 检查分类是否存在
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 更新分类
        category.setName(name);
        category.setSort(sort);
        category.setIcon(icon);

        categoryMapper.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查分类是否存在
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 检查是否有子分类
        List<Category> children = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId, id)
        );
        if (!children.isEmpty()) {
            throw new RuntimeException("请先删除子分类");
        }

        categoryMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        // 检查分类是否存在
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 更新状态
        category.setStatus(status);
        categoryMapper.updateById(category);
    }

    @Override
    public List<CategoryVO> listWithTree() {
        // 获取所有分类
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getSort)
        );

        // 转换为VO
        List<CategoryVO> categoryVOs = categories.stream()
                .map(category -> {
                    CategoryVO vo = new CategoryVO();
                    BeanUtils.copyProperties(category, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        // 构建树形结构
        Map<Long, List<CategoryVO>> parentMap = categoryVOs.stream()
                .collect(Collectors.groupingBy(CategoryVO::getParentId));

        categoryVOs.forEach(category -> {
            List<CategoryVO> children = parentMap.get(category.getId());
            if (children != null) {
                category.setChildren(children);
            } else {
                category.setChildren(new ArrayList<>());
            }
        });

        // 返回顶级分类
        return categoryVOs.stream()
                .filter(category -> category.getParentId() == 0)
                .collect(Collectors.toList());
    }
} 