package com.supermall.backend.domain.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.merchant.dto.MerchantRegisterRequest;
import com.supermall.backend.domain.merchant.dto.MerchantUpdateRequest;
import com.supermall.backend.domain.merchant.dto.MerchantResponse;
import com.supermall.backend.domain.merchant.entity.MerchantProfile;
import com.supermall.backend.domain.merchant.mapper.MerchantProfileMapper;
import com.supermall.backend.domain.merchant.service.MerchantProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantProfileServiceImpl extends ServiceImpl<MerchantProfileMapper, MerchantProfile> implements MerchantProfileService {

    @Override
    @Transactional
    public MerchantResponse register(Integer authId, MerchantRegisterRequest request) {
        // 检查是否已注册
        if (exists(new LambdaQueryWrapper<MerchantProfile>()
                .eq(MerchantProfile::getAuthId, authId))) {
            throw new BusinessException("该用户已注册为商家");
        }

        // 检查店铺名称是否已存在
        if (exists(new LambdaQueryWrapper<MerchantProfile>()
                .eq(MerchantProfile::getShopName, request.getShopName()))) {
            throw new BusinessException("店铺名称已存在");
        }

        MerchantProfile merchant = new MerchantProfile();
        BeanUtils.copyProperties(request, merchant);
        merchant.setAuthId(authId);
        merchant.setStatus(MerchantProfile.Status.PENDING);

        save(merchant);
        return convertToResponse(merchant);
    }

    @Override
    @Transactional
    public MerchantResponse update(Integer merchantId, MerchantUpdateRequest request) {
        MerchantProfile merchant = getById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家信息不存在");
        }

        // 检查店铺名称是否已被其他商家使用
        if (request.getShopName() != null && !request.getShopName().equals(merchant.getShopName())) {
            if (exists(new LambdaQueryWrapper<MerchantProfile>()
                    .eq(MerchantProfile::getShopName, request.getShopName()))) {
                throw new BusinessException("店铺名称已存在");
            }
            merchant.setShopName(request.getShopName());
        }

        if (request.getShopDescription() != null) {
            merchant.setShopDescription(request.getShopDescription());
        }
        if (request.getContactPhone() != null) {
            merchant.setContactPhone(request.getContactPhone());
        }
        if (request.getContactEmail() != null) {
            merchant.setContactEmail(request.getContactEmail());
        }

        updateById(merchant);
        return convertToResponse(merchant);
    }

    @Override
    public MerchantResponse getProfile(Integer merchantId) {
        MerchantProfile merchant = getById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家信息不存在");
        }
        return convertToResponse(merchant);
    }

    @Override
    @Transactional
    public void updateStatus(Integer merchantId, MerchantProfile.Status status) {
        MerchantProfile merchant = getById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家信息不存在");
        }

        // 验证状态转换的合法性
        validateStatusTransition(merchant.getStatus(), status);

        merchant.setStatus(status);
        updateById(merchant);
    }

    private void validateStatusTransition(MerchantProfile.Status currentStatus, MerchantProfile.Status newStatus) {
        if (currentStatus == newStatus) {
            return;
        }

        switch (currentStatus) {
            case PENDING -> {
                if (newStatus != MerchantProfile.Status.APPROVED && newStatus != MerchantProfile.Status.REJECTED) {
                    throw new BusinessException("待审核的商家只能审核通过或拒绝");
                }
            }
            case APPROVED -> {
                if (newStatus != MerchantProfile.Status.SUSPENDED && newStatus != MerchantProfile.Status.TERMINATED) {
                    throw new BusinessException("已审核的商家只能暂停或终止");
                }
            }
            case REJECTED -> {
                if (newStatus != MerchantProfile.Status.PENDING) {
                    throw new BusinessException("被拒绝的商家需要重新提交审核");
                }
            }
            case SUSPENDED -> {
                if (newStatus != MerchantProfile.Status.APPROVED && newStatus != MerchantProfile.Status.TERMINATED) {
                    throw new BusinessException("已暂停的商家只能恢复营业或终止合作");
                }
            }
            case TERMINATED -> {
                throw new BusinessException("已终止的商家不能更改状态");
            }
            default -> throw new BusinessException("非法的商家状态");
        }
    }

    @Override
    public Page<MerchantResponse> listMerchants(MerchantProfile.Status status, int page, int size) {
        Page<MerchantProfile> merchantPage = page(
                new Page<>(page, size),
                new LambdaQueryWrapper<MerchantProfile>()
                        .eq(status != null, MerchantProfile::getStatus, status)
                        .orderByDesc(MerchantProfile::getCreatedAt)
        );

        List<MerchantResponse> records = merchantPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        Page<MerchantResponse> responsePage = new Page<>();
        responsePage.setRecords(records);
        responsePage.setTotal(merchantPage.getTotal());
        responsePage.setCurrent(merchantPage.getCurrent());
        responsePage.setSize(merchantPage.getSize());

        return responsePage;
    }

    @Override
    public MerchantProfile getByAuthId(Integer authId) {
        MerchantProfile merchant = getOne(new LambdaQueryWrapper<MerchantProfile>()
                .eq(MerchantProfile::getAuthId, authId));
        
        if (merchant == null) {
            throw new BusinessException("商家信息不存在");
        }
        
        if (merchant.getStatus() != MerchantProfile.Status.APPROVED) {
            throw new BusinessException("商家未审核通过，无法执行此操作");
        }
        
        return merchant;
    }

    private MerchantResponse convertToResponse(MerchantProfile merchant) {
        MerchantResponse response = new MerchantResponse();
        BeanUtils.copyProperties(merchant, response);
        return response;
    }
} 