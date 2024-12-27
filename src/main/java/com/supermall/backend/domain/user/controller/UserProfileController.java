package com.supermall.backend.domain.user.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.user.dto.UserProfileRequest;
import com.supermall.backend.domain.user.dto.UserProfileResponse;
import com.supermall.backend.domain.user.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<UserProfileResponse> createProfile(@Valid @RequestBody UserProfileRequest request) {
        // TODO: 从SecurityContext获取authId
        Integer authId = 1;
        return Result.success(userProfileService.createProfile(authId, request));
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public Result<UserProfileResponse> updateProfile(@Valid @RequestBody UserProfileRequest request) {
        // TODO: 从SecurityContext获取userId
        Integer userId = 1;
        return Result.success(userProfileService.updateProfile(userId, request));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Result<UserProfileResponse> getProfile() {
        // TODO: 从SecurityContext获取userId
        Integer userId = 1;
        return Result.success(userProfileService.getProfile(userId));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deleteProfile() {
        // TODO: 从SecurityContext获取userId
        Integer userId = 1;
        userProfileService.deleteProfile(userId);
        return Result.success();
    }
} 