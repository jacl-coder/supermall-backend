package com.supermall.backend.domain.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.PageResult;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.common.security.annotation.RequirePermission;
import com.supermall.backend.common.security.model.SecurityUser;
import com.supermall.backend.domain.product.dto.ProductRequest;
import com.supermall.backend.domain.product.dto.ProductResponse;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品管理", description = "商品相关接口，包含商品的增删改查等基本操作以及商品状态管理")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @RequirePermission(role = "MERCHANT", requireMerchant = true)
    public Result<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        Product product = productService.createProduct(request, user.getMerchantId());
        return Result.success(convertToResponse(product));
    }

    @PutMapping("/{id}")
    @RequirePermission(role = "MERCHANT", requireMerchant = true)
    public Result<ProductResponse> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        // 验证商品所属权
        Product existingProduct = productService.getById(id);
        if (existingProduct == null) {
            return Result.fail("商品不存在");
        }
        if (!existingProduct.getMerchantId().equals(user.getMerchantId())) {
            throw new BusinessException("无权操作此商品");
        }

        Product product = productService.updateProduct(id, request);
        return Result.success(convertToResponse(product));
    }

    @GetMapping("/{id}")
    public Result<ProductResponse> getProduct(@PathVariable Integer id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.fail("商品不存在");
        }
        return Result.success(convertToResponse(product));
    }

    @GetMapping("/category/{categoryId}")
    public Result<PageResult<ProductResponse>> getProductsByCategory(
            @PathVariable Integer categoryId,
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

    @PutMapping("/{id}/status")
    @RequirePermission(requireMerchant = true)
    public Result<ProductResponse> updateProductStatus(
            @PathVariable Integer id,
            @RequestParam Product.Status status,
            @AuthenticationPrincipal SecurityUser user) {
        // 验证商品所属权
        Product existingProduct = productService.getById(id);
        if (existingProduct == null) {
            return Result.fail("商品不存在");
        }

        // 商家只能操作自己的商品，管理员可以操作所有商品
        if (user.isMerchant() && !existingProduct.getMerchantId().equals(user.getMerchantId())) {
            throw new BusinessException("无权操作此商品");
        }

        // 商家不能直接审核商品
        if (user.isMerchant() && (status == Product.Status.APPROVED || status == Product.Status.REJECTED)) {
            throw new BusinessException("商家不能审核商品");
        }

        Product product = productService.updateProductStatus(id, status);
        return Result.success(convertToResponse(product));
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