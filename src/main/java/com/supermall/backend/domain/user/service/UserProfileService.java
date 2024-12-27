package com.supermall.backend.domain.user.service;

import com.supermall.backend.domain.user.dto.UserProfileRequest;
import com.supermall.backend.domain.user.dto.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse createProfile(Long authId, UserProfileRequest request);
    UserProfileResponse updateProfile(Long userId, UserProfileRequest request);
    UserProfileResponse getProfile(Long userId);
    UserProfileResponse getProfileByAuthId(Long authId);
    void deleteProfile(Long userId);
} 