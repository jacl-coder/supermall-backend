package com.supermall.backend.domain.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.common.security.util.JwtUtil;
import com.supermall.backend.domain.auth.dto.*;
import com.supermall.backend.domain.auth.entity.AuthUser;
import com.supermall.backend.domain.auth.mapper.AuthUserMapper;
import com.supermall.backend.domain.auth.service.AuthUserService;
import com.supermall.backend.domain.auth.service.RoleService;
import com.supermall.backend.domain.user.entity.UserProfile;
import com.supermall.backend.domain.user.service.UserProfileService;
import com.supermall.backend.domain.user.dto.UserProfileRequest;
import com.supermall.backend.domain.user.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl extends ServiceImpl<AuthUserMapper, AuthUser> implements AuthUserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RoleService roleService;
    private final UserProfileService userProfileService;

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }

        // 创建认证用户
        AuthUser authUser = new AuthUser();
        authUser.setUsername(request.getUsername());
        authUser.setEmail(request.getEmail());
        authUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        authUser.setStatus("ACTIVE");
        authUser.setRoleId(3); // ROLE_USER 的 ID
        save(authUser);

        // 创建用户信息
        UserProfileRequest profileRequest = new UserProfileRequest();
        profileRequest.setFullName(request.getFullName());
        profileRequest.setPhoneNumber(request.getPhoneNumber());
        userProfileService.createProfile(authUser.getId(), profileRequest);

        // 更新最后登录时间
        updateLastLogin(authUser.getId());

        // 获取最新的用户信息
        authUser = getById(authUser.getId());
        UserProfileResponse profile = userProfileService.getProfileByAuthId(authUser.getId());
        UserInfoResponse userInfo = convertToUserInfoResponse(authUser, profile);

        // 生成 token 并返回登录响应
        String token = jwtUtil.generateToken(authUser.getUsername(), authUser.getId(), authUser.getRoleId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserInfo(userInfo);
        return response;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        AuthUser authUser = getByUsername(request.getUsername());

        if (authUser == null || !passwordEncoder.matches(request.getPassword(), authUser.getPasswordHash())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!"ACTIVE".equals(authUser.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }

        // 更新最后登录时间
        updateLastLogin(authUser.getId());

        // 获取用户信息
        UserInfoResponse userInfo;
        try {
            var profile = userProfileService.getProfileByAuthId(authUser.getId());
            userInfo = convertToUserInfoResponse(authUser, profile);
        } catch (BusinessException e) {
            // 如果用户资料不存在，只返回基本信息
            userInfo = convertToUserInfoResponse(authUser, null);
        }

        // 生成 token
        String token = jwtUtil.generateToken(authUser.getUsername(), authUser.getId(), authUser.getRoleId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserInfo(userInfo);
        return response;
    }

    @Override
    public boolean existsByUsername(String username) {
        return exists(new LambdaQueryWrapper<AuthUser>()
                .eq(AuthUser::getUsername, username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return exists(new LambdaQueryWrapper<AuthUser>()
                .eq(AuthUser::getEmail, email));
    }

    @Override
    public AuthUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<AuthUser>()
                .eq(AuthUser::getUsername, username));
    }

    @Override
    @Transactional
    public void updatePassword(Integer authId, String oldPassword, String newPassword) {
        AuthUser authUser = getById(authId);
        if (authUser == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, authUser.getPasswordHash())) {
            throw new BusinessException("原密码错误");
        }

        authUser.setPasswordHash(passwordEncoder.encode(newPassword));
        updateById(authUser);
    }

    @Override
    public void updateStatus(Integer authId, String status) {
        AuthUser authUser = getById(authId);
        if (authUser == null) {
            throw new BusinessException("用户不存在");
        }

        authUser.setStatus(status);
        updateById(authUser);
    }

    @Override
    public void updateLastLogin(Integer authId) {
        AuthUser authUser = getById(authId);
        if (authUser != null) {
            authUser.setLastLogin(LocalDateTime.now());
            updateById(authUser);
        }
    }

    @Override
    public void logout(Integer authId) {
        // 可在这里添加登出相关的业务逻辑
        // 比如记录登出时间、清除用户token等
        // 目前数据库表中没有相关字段，所以暂时不做处理
    }

    private UserInfoResponse convertToUserInfoResponse(AuthUser authUser) {
        return convertToUserInfoResponse(authUser, null);
    }

    private UserInfoResponse convertToUserInfoResponse(AuthUser authUser, UserProfileResponse userProfile) {
        UserInfoResponse response = new UserInfoResponse();
        response.setId(authUser.getId());
        response.setUsername(authUser.getUsername());
        response.setEmail(authUser.getEmail());
        response.setLastLogin(authUser.getLastLogin());
        
        if (userProfile != null) {
            response.setFullName(userProfile.getFullName());
            response.setPhoneNumber(userProfile.getPhoneNumber());
            response.setAvatarUrl(userProfile.getAvatarUrl());
        }

        // 设置角色和权限
        String roleName = roleService.getRole(authUser.getRoleId()).getName();
        response.setRole(roleName);
        response.setPermissions(roleService.getRolePermissionCodes(authUser.getRoleId()));

        return response;
    }
} 