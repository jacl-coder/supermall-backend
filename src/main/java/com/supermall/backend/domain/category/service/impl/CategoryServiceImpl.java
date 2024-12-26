package com.supermall.backend.domain.category.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.category.entity.Category;
import com.supermall.backend.domain.category.mapper.CategoryMapper;
import com.supermall.backend.domain.category.service.CategoryService;
import com.supermall.backend.domain.category.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    
    @Override
    public List<CategoryVO> listCategories() {
        log.debug("获取分类列表");
        try {
            List<CategoryVO> categories = baseMapper.listCategories();
            return buildTree(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            throw new ApiException("获取分类列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public CategoryVO getDetail(Long id) {
        log.debug("获取分类详情: id={}", id);
        try {
            CategoryVO category = baseMapper.getCategoryById(id);
            if (category == null) {
                throw new ApiException("分类不存在");
            }
            
            // 获取子分类
            List<Category> children = listChildren(id);
            if (!children.isEmpty()) {
                List<CategoryVO> childrenVOs = children.stream()
                    .map(child -> {
                        CategoryVO vo = new CategoryVO();
                        vo.setId(child.getId());
                        vo.setName(child.getName());
                        vo.setParentId(child.getParentId());
                        vo.setLevel(child.getLevel());
                        vo.setSort(child.getSort());
                        vo.setStatus(child.getStatus());
                        vo.setCreatedTime(child.getCreatedTime());
                        vo.setUpdatedTime(child.getUpdatedTime());
                        return vo;
                    })
                    .collect(Collectors.toList());
                category.setChildren(childrenVOs);
            }
            
            return category;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取分类详情失败", e);
            throw new ApiException("获取分类详情失败: " + e.getMessage());
        }
    }
    
    @Override
    public void updateStatus(Long id, Integer status) {
        log.debug("更新分类状态: id={}, status={}", id, status);
        try {
            Category category = getById(id);
            if (category == null) {
                throw new ApiException("分类不存在");
            }
            
            category.setStatus(status);
            updateById(category);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新分类状态失败", e);
            throw new ApiException("更新分类状态失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Category> listChildren(Long parentId) {
        log.debug("获取子分类列表: parentId={}", parentId);
        try {
            return baseMapper.listChildren(parentId);
        } catch (Exception e) {
            log.error("获取子分类列表失败", e);
            throw new ApiException("获取子分类列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<CategoryVO> buildTree(List<CategoryVO> categories) {
        // 按父ID分组
        Map<Long, List<CategoryVO>> childrenMap = categories.stream()
            .filter(cat -> cat.getParentId() != null && cat.getParentId() != 0)
            .collect(Collectors.groupingBy(CategoryVO::getParentId));
        
        // 设置子分类
        categories.forEach(cat -> cat.setChildren(childrenMap.get(cat.getId())));
        
        // 返回顶级分类
        return categories.stream()
            .filter(cat -> cat.getParentId() == null || cat.getParentId() == 0)
            .collect(Collectors.toList());
    }
} 