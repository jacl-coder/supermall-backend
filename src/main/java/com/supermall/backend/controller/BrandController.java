package com.supermall.backend.controller;

import com.supermall.backend.common.api.CommonResult;
import com.supermall.backend.domain.brand.entity.Brand;
import com.supermall.backend.domain.brand.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌控制器
 */
@Tag(name = "品牌管理", description = "品牌管理接口")
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @Operation(summary = "获取品牌列表")
    @GetMapping
    public CommonResult<List<Brand>> list() {
        List<Brand> brands = brandService.list();
        return CommonResult.success(brands);
    }

    @Operation(summary = "创建品牌")
    @PostMapping
    public CommonResult<Void> create(@RequestBody CreateBrandParam param) {
        brandService.create(param.getName(), param.getLogo(), param.getDescription(), param.getSort());
        return CommonResult.success(null);
    }

    @Operation(summary = "更新品牌")
    @PutMapping("/{id}")
    public CommonResult<Void> update(@PathVariable Long id, @RequestBody UpdateBrandParam param) {
        brandService.update(id, param.getName(), param.getLogo(), param.getDescription(), param.getSort());
        return CommonResult.success(null);
    }

    @Operation(summary = "删除品牌")
    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return CommonResult.success(null);
    }

    @Operation(summary = "更新品牌状态")
    @PutMapping("/{id}/status")
    public CommonResult<Void> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusParam param) {
        brandService.updateStatus(id, param.getStatus());
        return CommonResult.success(null);
    }

    @Data
    public static class CreateBrandParam {
        private String name;
        private String logo;
        private String description;
        private Integer sort;
    }

    @Data
    public static class UpdateBrandParam {
        private String name;
        private String logo;
        private String description;
        private Integer sort;
    }

    @Data
    public static class UpdateStatusParam {
        private Integer status;
    }
} 