package com.supermall.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.CommonResult;
import com.supermall.backend.domain.brand.entity.Brand;
import com.supermall.backend.domain.brand.service.BrandService;
import com.supermall.backend.domain.brand.vo.BrandVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌控制器
 */
@Slf4j
@Tag(name = "品牌管理", description = "品牌管理接口")
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @Operation(summary = "创建品牌")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Brand> create(@RequestBody Brand brand) {
        brandService.save(brand);
        return CommonResult.success(brand);
    }

    @Operation(summary = "分页查询品牌")
    @GetMapping
    public CommonResult<Page<BrandVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        try {
            log.debug("分页查询品牌: pageNum={}, pageSize={}, keyword={}", pageNum, pageSize, keyword);
            Page<BrandVO> page = brandService.page(pageNum, pageSize, keyword);
            return CommonResult.success(page);
        } catch (Exception e) {
            log.error("分页查询品牌失败", e);
            return CommonResult.failed("分页查询品牌失败");
        }
    }

    @Operation(summary = "更新品牌")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Void> update(@PathVariable Long id, @RequestBody Brand brand) {
        brand.setId(id);
        brandService.updateById(brand);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除品牌")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Void> delete(@PathVariable Long id) {
        brandService.removeById(id);
        return CommonResult.success(null);
    }

    @Operation(summary = "批量删除品牌")
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Void> deleteBatch(@RequestBody List<Long> ids) {
        brandService.deleteBatch(ids);
        return CommonResult.success(null);
    }

    @Operation(summary = "更新品牌状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        brandService.updateStatus(id, status);
        return CommonResult.success(null);
    }

    @Operation(summary = "获取所有品牌")
    @GetMapping("/all")
    public CommonResult<List<BrandVO>> listAll() {
        return CommonResult.success(brandService.listAll());
    }
} 