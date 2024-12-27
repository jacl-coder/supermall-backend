package com.supermall.backend.domain.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.domain.auth.entity.UserProfile;
import com.supermall.backend.domain.auth.mapper.UserProfileMapper;
import com.supermall.backend.domain.auth.service.UserProfileService;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

    @Override
    public UserProfile getByAuthId(Long authId) {
        return getOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getAuthId, authId));
    }

    @Override
    public UserProfile getByUserId(Long userId) {
        return getById(userId);
    }
} 