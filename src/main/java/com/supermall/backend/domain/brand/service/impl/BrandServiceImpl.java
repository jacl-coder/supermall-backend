package com.supermall.backend.domain.brand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.brand.entity.Brand;
import com.supermall.backend.domain.brand.mapper.BrandMapper;
import com.supermall.backend.domain.brand.service.BrandService;
import com.supermall.backend.domain.brand.vo.BrandVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    @Override
    public Page<BrandVO> page(Integer pageNum, Integer pageSize, String keyword) {
        log.debug("分页查询品牌: pageNum={}, pageSize={}, keyword={}", pageNum, pageSize, keyword);
        
        try {
            // 1. 先查询原始数据
            Page<Brand> brandPage = page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Brand>()
                    .like(StringUtils.hasText(keyword), Brand::getName, keyword)
                    .orderByAsc(Brand::getSort)
            );
            
            log.debug("查询到的原始数据: {}", brandPage);
            
            // 2. 转换为VO对象
            Page<BrandVO> voPage = new Page<>();
            voPage.setCurrent(brandPage.getCurrent());
            voPage.setSize(brandPage.getSize());
            voPage.setTotal(brandPage.getTotal());
            voPage.setPages(brandPage.getPages());
            
            if (brandPage.getRecords() != null) {
                voPage.setRecords(brandPage.getRecords().stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList()));
            }
            
            return voPage;
        } catch (Exception e) {
            log.error("分页查询品牌失败", e);
            throw new ApiException("分页查询品牌失败");
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Brand brand = getById(id);
        if (brand == null) {
            throw new ApiException("品牌不存在");
        }
        
        brand.setStatus(status);
        updateById(brand);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        removeByIds(ids);
    }

    @Override
    public List<BrandVO> listAll() {
        return list(new LambdaQueryWrapper<Brand>()
                .eq(Brand::getStatus, 1)
                .orderByAsc(Brand::getSort))
            .stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    private BrandVO convertToVO(Brand brand) {
        BrandVO vo = new BrandVO();
        BeanUtils.copyProperties(brand, vo);
        return vo;
    }
} 