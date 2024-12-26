package com.supermall.backend.controller;

import com.supermall.backend.common.api.CommonResult;
import com.supermall.backend.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "认证管理接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public CommonResult<Void> register(@RequestBody RegisterParam param) {
        userService.register(param.getUsername(), param.getPassword(), param.getEmail());
        return CommonResult.success(null);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public CommonResult<Map<String, String>> login(@RequestBody @Valid LoginParam param) {
        String token = userService.login(param.getUsername(), param.getPassword());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        return CommonResult.success(tokenMap);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public CommonResult<Map<String, String>> refreshToken(@RequestBody RefreshTokenParam param) {
        String token = userService.refreshToken(param.getToken());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        return CommonResult.success(tokenMap);
    }

    @Data
    public static class RegisterParam {
        private String username;
        private String password;
        private String email;
    }

    @Data
    public static class LoginParam {
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @Data
    public static class RefreshTokenParam {
        private String token;
    }
} 