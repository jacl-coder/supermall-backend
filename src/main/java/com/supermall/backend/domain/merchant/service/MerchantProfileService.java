package com.supermall.backend.domain.merchant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.merchant.dto.MerchantRegisterRequest;
import com.supermall.backend.domain.merchant.dto.MerchantUpdateRequest;
import com.supermall.backend.domain.merchant.dto.MerchantResponse;

public interface MerchantProfileService {
    MerchantResponse register(Long authId, MerchantRegisterRequest request);
    MerchantResponse update(Long merchantId, MerchantUpdateRequest request);
    MerchantResponse getProfile(Long merchantId);
    MerchantResponse getProfileByAuthId(Long authId);
    void updateStatus(Long merchantId, String status);
    Page<MerchantResponse> listMerchants(String status, int page, int size);
} 