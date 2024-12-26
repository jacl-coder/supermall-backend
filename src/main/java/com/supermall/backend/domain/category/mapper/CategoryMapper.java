package com.supermall.backend.domain.category.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.domain.category.entity.Category;
import com.supermall.backend.domain.category.vo.CategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 商品分类Mapper接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    
    /**
     * 获取分类列表（包含父分类名称）
     */
    List<CategoryVO> listCategories();
    
    /**
     * 获取分类详情（包含父分类名称）
     */
    CategoryVO getCategoryById(@Param("id") Long id);
    
    /**
     * 获取子分类列表
     */
    List<Category> listChildren(@Param("parentId") Long parentId);
} 