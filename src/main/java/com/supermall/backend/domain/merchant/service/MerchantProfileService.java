package com.supermall.backend.domain.merchant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.merchant.dto.MerchantRegisterRequest;
import com.supermall.backend.domain.merchant.dto.MerchantUpdateRequest;
import com.supermall.backend.domain.merchant.dto.MerchantResponse;
import com.supermall.backend.domain.merchant.entity.MerchantProfile;

public interface MerchantProfileService {
    MerchantResponse register(Integer authId, MerchantRegisterRequest request);
    MerchantResponse update(Integer merchantId, MerchantUpdateRequest request);
    MerchantResponse getProfile(Integer merchantId);
    void updateStatus(Integer merchantId, MerchantProfile.Status status);
    Page<MerchantResponse> listMerchants(MerchantProfile.Status status, int page, int size);
} 