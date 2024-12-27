package com.supermall.backend.domain.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.auth.entity.UserProfile;

public interface UserProfileService extends IService<UserProfile> {
    UserProfile getByAuthId(Long authId);
    UserProfile getByUserId(Long userId);
} 