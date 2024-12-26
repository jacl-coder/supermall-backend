package com.supermall.backend.domain.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.vo.ProductVO;
import java.util.List;

public interface ProductService extends IService<Product> {
    /**
     * 分页查询商品
     */
    Page<ProductVO> page(Integer pageNum, Integer pageSize, String keyword);
    
    /**
     * 更新商品状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 批量删除商品
     */
    void deleteBatch(List<Long> ids);
    
    /**
     * 获取商品详情
     */
    ProductVO getDetail(Long id);
} 