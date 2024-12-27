package com.supermall.backend.domain.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.user.dto.UserProfileRequest;
import com.supermall.backend.domain.user.dto.UserProfileResponse;
import com.supermall.backend.domain.user.entity.UserProfile;
import com.supermall.backend.domain.user.mapper.UserProfileMapper;
import com.supermall.backend.domain.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

    @Override
    @Transactional
    public UserProfileResponse createProfile(Integer authId, UserProfileRequest request) {
        // 检查是否已存在
        if (exists(new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getAuthId, authId))) {
            throw new BusinessException("用户信息已存在");
        }

        UserProfile profile = new UserProfile();
        profile.setAuthId(authId);
        profile.setFullName(request.getFullName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setAvatarUrl(request.getAvatarUrl());

        save(profile);
        return convertToResponse(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Integer userId, UserProfileRequest request) {
        UserProfile profile = getById(userId);
        if (profile == null) {
            throw new BusinessException("用户信息不存在");
        }

        profile.setFullName(request.getFullName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setAvatarUrl(request.getAvatarUrl());

        updateById(profile);
        return convertToResponse(profile);
    }

    @Override
    public UserProfileResponse getProfile(Integer userId) {
        UserProfile profile = getById(userId);
        if (profile == null) {
            throw new BusinessException("用户信息不存在");
        }
        return convertToResponse(profile);
    }

    @Override
    public UserProfileResponse getProfileByAuthId(Integer authId) {
        UserProfile profile = getOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getAuthId, authId));
        if (profile == null) {
            throw new BusinessException("用户信息不存在");
        }
        return convertToResponse(profile);
    }

    @Override
    @Transactional
    public void deleteProfile(Integer userId) {
        if (!removeById(userId)) {
            throw new BusinessException("用户信息不存在");
        }
    }

    private UserProfileResponse convertToResponse(UserProfile profile) {
        UserProfileResponse response = new UserProfileResponse();
        BeanUtils.copyProperties(profile, response);
        return response;
    }
} 