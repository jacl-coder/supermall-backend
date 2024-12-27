package com.supermall.backend.controller;

import com.supermall.backend.common.api.CommonResult;
import com.supermall.backend.domain.user.dto.RegisterDTO;
import com.supermall.backend.domain.user.entity.User;
import com.supermall.backend.domain.user.service.UserService;
import com.supermall.backend.domain.user.service.impl.UserServiceImpl;
import com.supermall.backend.domain.user.vo.UserVO;
import com.supermall.backend.common.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
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
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public CommonResult<UserVO> register(@RequestBody RegisterDTO registerDTO) {
        try {
            // 1. 基本验证
            if (StringUtils.isEmpty(registerDTO.getUsername()) 
                || StringUtils.isEmpty(registerDTO.getPassword())) {
                return CommonResult.failed("用户名和密码不能为空");
            }
            
            // 2. 创建用户
            User user = new User();
            user.setUsername(registerDTO.getUsername());
            user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            user.setEmail(registerDTO.getEmail());
            
            // 3. 设置角色
            String roleCode = registerDTO.getRoleCode();
            if (StringUtils.isEmpty(roleCode)) {
                roleCode = "USER"; // 默认为普通用户
            }
            
            UserVO userVO = userService.register(user, roleCode);
            return CommonResult.success(userVO);
        } catch (ApiException e) {
            return CommonResult.failed(e.getMessage());
        }
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