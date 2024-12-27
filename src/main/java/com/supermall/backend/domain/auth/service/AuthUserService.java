package com.supermall.backend.domain.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.auth.dto.LoginRequest;
import com.supermall.backend.domain.auth.dto.LoginResponse;
import com.supermall.backend.domain.auth.dto.RegisterRequest;
import com.supermall.backend.domain.auth.entity.AuthUser;

public interface AuthUserService extends IService<AuthUser> {
    // 用户注册
    LoginResponse register(RegisterRequest request);
    
    // 用户登录
    LoginResponse login(LoginRequest request);
    
    // 用户登出
    void logout(Integer authId);
    
    // 检查用户名是否存在
    boolean existsByUsername(String username);
    
    // 检查邮箱是否存在
    boolean existsByEmail(String email);
    
    // 根据用户名查找用户
    AuthUser getByUsername(String username);
    
    // 更新密码
    void updatePassword(Integer authId, String oldPassword, String newPassword);
    
    // 更新用户状态
    void updateStatus(Integer authId, String status);
    
    // 更新最后登录时间
    void updateLastLogin(Integer authId);
} 