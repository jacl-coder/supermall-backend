package com.supermall.backend.domain.category.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.category.entity.Category;
import com.supermall.backend.domain.category.vo.CategoryVO;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface CategoryService extends IService<Category> {
    /**
     * 创建分类
     *
     * @param name     分类名称
     * @param parentId 父级分类ID
     * @param sort     排序
     * @param icon     图标
     */
    void create(String name, Long parentId, Integer sort, String icon);

    /**
     * 更新分类
     *
     * @param id   分类ID
     * @param name 分类名称
     * @param sort 排序
     * @param icon 图标
     */
    void update(Long id, String name, Integer sort, String icon);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void delete(Long id);

    /**
     * 更新分类状态
     *
     * @param id     分类ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 获取分类树
     *
     * @return 分类树
     */
    List<CategoryVO> listWithTree();
} 