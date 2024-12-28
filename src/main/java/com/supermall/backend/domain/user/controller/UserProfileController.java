package com.supermall.backend.domain.user.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.user.dto.UserProfileRequest;
import com.supermall.backend.domain.user.dto.UserProfileResponse;
import com.supermall.backend.domain.user.service.UserProfileService;
import com.supermall.backend.common.security.model.SecurityUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户资料管理", description = "包括用户基本信息的创建、修改、查询和删除，用于管理用户的个人资料信息")
@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<UserProfileResponse> createProfile(
            @Valid @RequestBody UserProfileRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(userProfileService.createProfile(user.getId(), request));
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public Result<UserProfileResponse> updateProfile(
            @Valid @RequestBody UserProfileRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(userProfileService.updateProfile(user.getId(), request));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Result<UserProfileResponse> getProfile(@AuthenticationPrincipal SecurityUser user) {
        return Result.success(userProfileService.getProfile(user.getId()));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deleteProfile(@AuthenticationPrincipal SecurityUser user) {
        userProfileService.deleteProfile(user.getId());
        return Result.success();
    }
} 