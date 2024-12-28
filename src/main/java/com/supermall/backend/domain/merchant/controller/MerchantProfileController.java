package com.supermall.backend.domain.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.common.security.annotation.RequirePermission;
import com.supermall.backend.common.security.model.SecurityUser;
import com.supermall.backend.domain.merchant.dto.MerchantRegisterRequest;
import com.supermall.backend.domain.merchant.dto.MerchantUpdateRequest;
import com.supermall.backend.domain.merchant.dto.MerchantResponse;
import com.supermall.backend.domain.merchant.entity.MerchantProfile;
import com.supermall.backend.domain.merchant.service.MerchantProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商家管理", description = "商家账户管理功能，包含注册、更新、查询、审核及状态管理等操作")
@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantProfileController {

    private final MerchantProfileService merchantService;

    @Operation(summary = "商家注册")
    @PostMapping("/register")
    @RequirePermission(role = "USER")
    public Result<MerchantResponse> register(
            @Valid @RequestBody MerchantRegisterRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(merchantService.register(user.getId(), request));
    }

    @Operation(summary = "更新商家资料")
    @PutMapping("/profile")
    @RequirePermission(role = "MERCHANT")
    public Result<MerchantResponse> updateProfile(
            @Valid @RequestBody MerchantUpdateRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(merchantService.update(user.getMerchantId(), request));
    }

    @Operation(summary = "获取商家资料")
    @GetMapping("/profile")
    @RequirePermission(role = "MERCHANT")
    public Result<MerchantResponse> getProfile(
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(merchantService.getProfile(user.getMerchantId()));
    }

    @Operation(summary = "更新商家状态")
    @PutMapping("/{merchantId}/status")
    @RequirePermission(role = "ADMIN")
    public Result<Void> updateStatus(
            @PathVariable Integer merchantId,
            @RequestParam MerchantProfile.Status status) {
        merchantService.updateStatus(merchantId, status);
        return Result.success();
    }

    @Operation(summary = "查询商家列表")
    @GetMapping
    @RequirePermission(role = "ADMIN")
    public Result<Page<MerchantResponse>> listMerchants(
            @RequestParam(required = false) MerchantProfile.Status status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(merchantService.listMerchants(status, page, size));
    }
} 