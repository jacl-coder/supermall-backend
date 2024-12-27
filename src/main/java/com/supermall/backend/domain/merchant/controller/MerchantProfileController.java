package com.supermall.backend.domain.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.merchant.dto.MerchantRegisterRequest;
import com.supermall.backend.domain.merchant.dto.MerchantUpdateRequest;
import com.supermall.backend.domain.merchant.dto.MerchantResponse;
import com.supermall.backend.domain.merchant.service.MerchantProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商家管理", description = "商家账户管理功能，包含注册、更新、查询、审核及状态管理等操作")
@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantProfileController {

    private final MerchantProfileService merchantService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('USER')")
    public Result<MerchantResponse> register(@Valid @RequestBody MerchantRegisterRequest request) {
        // TODO: 从SecurityContext获取authId
        Long authId = 1L;
        return Result.success(merchantService.register(authId, request));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<MerchantResponse> updateProfile(@Valid @RequestBody MerchantUpdateRequest request) {
        // TODO: 从SecurityContext获取merchantId
        Long merchantId = 1L;
        return Result.success(merchantService.update(merchantId, request));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<MerchantResponse> getProfile() {
        Long merchantId = 1L;
        return Result.success(merchantService.getProfile(merchantId));
    }

    @PutMapping("/{merchantId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStatus(
            @PathVariable Long merchantId,
            @RequestParam String status) {
        merchantService.updateStatus(merchantId, status);
        return Result.success();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<MerchantResponse>> listMerchants(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(merchantService.listMerchants(status, page, size));
    }
} 