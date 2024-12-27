package com.supermall.backend.domain.user.service;

import com.supermall.backend.domain.user.dto.UserProfileRequest;
import com.supermall.backend.domain.user.dto.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse createProfile(Integer authId, UserProfileRequest request);
    UserProfileResponse updateProfile(Integer userId, UserProfileRequest request);
    UserProfileResponse getProfile(Integer userId);
    UserProfileResponse getProfileByAuthId(Integer authId);
    void deleteProfile(Integer userId);
} 