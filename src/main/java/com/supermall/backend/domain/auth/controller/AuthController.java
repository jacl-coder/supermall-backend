package com.supermall.backend.domain.auth.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.auth.dto.*;
import com.supermall.backend.domain.auth.service.AuthUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证授权管理", description = "包括用户登录、注册、密码重置、token刷新等认证授权功能")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUserService authUserService;

    @PostMapping("/register")
    public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse loginResponse = authUserService.register(request);
        return Result.success(convertToAuthResponse(loginResponse));
    }

    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authUserService.login(request);
        return Result.success(convertToAuthResponse(loginResponse));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Integer authId = Integer.parseInt(authentication.getName());
            authUserService.logout(authId);
        }
        SecurityContextHolder.clearContext();
        return Result.success();
    }

    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        return Result.success(!authUserService.existsByUsername(username));
    }

    @PostMapping("/update-password")
    public Result<Void> updatePassword(@RequestBody PasswordUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.fail("未登录");
        }
        Integer authId = Integer.parseInt(authentication.getName());
        authUserService.updatePassword(authId, request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }

    private AuthResponse convertToAuthResponse(LoginResponse loginResponse) {
        AuthResponse response = new AuthResponse();
        UserInfoResponse userInfo = loginResponse.getUserInfo();
        
        response.setId(userInfo.getId());
        response.setUsername(userInfo.getUsername());
        response.setEmail(userInfo.getEmail());
        response.setFullName(userInfo.getFullName());
        response.setPhoneNumber(userInfo.getPhoneNumber());
        response.setAvatarUrl(userInfo.getAvatarUrl());
        response.setRole(userInfo.getRole());
        response.setToken(loginResponse.getToken());
        
        return response;
    }
} 