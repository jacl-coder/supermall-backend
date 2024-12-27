package com.supermall.backend.domain.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.PageResult;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.product.dto.ProductRequest;
import com.supermall.backend.domain.product.dto.ProductResponse;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        // TODO: 从SecurityContext获取商家ID
        Long merchantId = 1L; // 临时写死，后续从登录用户获取
        Product product = productService.createProduct(request, merchantId);
        return Result.success(convertToResponse(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        Product product = productService.updateProduct(id, request);
        if (product == null) {
            return Result.fail("商品不存在");
        }
        return Result.success(convertToResponse(product));
    }

    @GetMapping("/{id}")
    public Result<ProductResponse> getProduct(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.fail("商品不存在");
        }
        return Result.success(convertToResponse(product));
    }

    @GetMapping("/category/{categoryId}")
    public Result<PageResult<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Product> productPage = productService.getProductsByCategory(categoryId, page, size);
        return Result.success(convertToPageResult(productPage));
    }

    @GetMapping("/search")
    public Result<PageResult<ProductResponse>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Product> productPage = productService.searchProducts(keyword, page, size);
        return Result.success(convertToPageResult(productPage));
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response);
        return response;
    }

    private PageResult<ProductResponse> convertToPageResult(Page<Product> page) {
        Page<ProductResponse> responsePage = new Page<>();
        responsePage.setRecords(page.getRecords().stream()
                .map(this::convertToResponse)
                .toList());
        responsePage.setTotal(page.getTotal());
        responsePage.setCurrent(page.getCurrent());
        responsePage.setSize(page.getSize());
        
        return PageResult.from(responsePage);
    }
} 