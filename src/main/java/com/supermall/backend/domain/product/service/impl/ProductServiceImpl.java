package com.supermall.backend.domain.product.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.brand.entity.Brand;
import com.supermall.backend.domain.brand.service.BrandService;
import com.supermall.backend.domain.category.entity.Category;
import com.supermall.backend.domain.category.service.CategoryService;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.mapper.ProductMapper;
import com.supermall.backend.domain.product.service.ProductService;
import com.supermall.backend.domain.product.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    private final CategoryService categoryService;
    private final BrandService brandService;

    public ProductServiceImpl(CategoryService categoryService, BrandService brandService) {
        this.categoryService = categoryService;
        this.brandService = brandService;
    }

    @Override
    public Page<ProductVO> page(Integer pageNum, Integer pageSize, String keyword) {
        log.debug("分页查询商品: pageNum={}, pageSize={}, keyword={}", pageNum, pageSize, keyword);
        
        try {
            Page<Product> page = new Page<>(pageNum, pageSize);
            IPage<ProductVO> voPage = baseMapper.pageProducts(page, keyword);
            log.debug("查询到的数据: {}", voPage);
            
            // 转换为 Page 对象
            Page<ProductVO> resultPage = new Page<>();
            resultPage.setRecords(voPage.getRecords());
            resultPage.setTotal(voPage.getTotal());
            resultPage.setCurrent(voPage.getCurrent());
            resultPage.setSize(voPage.getSize());
            
            return resultPage;
        } catch (Exception e) {
            log.error("分页查询商品失败", e);
            throw new ApiException("分页查询商品失败: " + e.getMessage());
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = getById(id);
        if (product == null) {
            throw new ApiException("商品不存在");
        }
        
        product.setStatus(status);
        updateById(product);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        removeByIds(ids);
    }

    @Override
    public ProductVO getDetail(Long id) {
        log.debug("获取商品详情: id={}", id);
        
        try {
            // 1. 获取商品基本信息
            Product product = getById(id);
            if (product == null) {
                throw new ApiException("商品不存在");
            }
            
            // 2. 转换为 VO 对象
            ProductVO vo = new ProductVO();
            // 复制基本属性
            BeanUtils.copyProperties(product, vo);
            
            // 3. 查询分类名称
            Category category = categoryService.getById(product.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
                log.debug("获取到分类名称: {}", category.getName());
            } else {
                log.warn("未找到分类信息: categoryId={}", product.getCategoryId());
            }
            
            // 4. 查询品牌名称
            Brand brand = brandService.getById(product.getBrandId());
            if (brand != null) {
                vo.setBrandName(brand.getName());
                log.debug("获取到品牌名称: {}", brand.getName());
            } else {
                log.warn("未找到品牌信息: brandId={}", product.getBrandId());
            }
            
            // 5. 处理子图片
            if (product.getSubImages() != null && !product.getSubImages().isEmpty()) {
                vo.setSubImagesList(Arrays.asList(product.getSubImages().split(",")));
            }
            
            log.debug("商品详情: {}", vo);
            return vo;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取商品详情失败", e);
            throw new ApiException("获取商品详情失败: " + e.getMessage());
        }
    }
} 