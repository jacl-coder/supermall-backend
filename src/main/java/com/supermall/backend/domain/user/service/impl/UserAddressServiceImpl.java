package com.supermall.backend.domain.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.user.dto.AddressRequest;
import com.supermall.backend.domain.user.dto.AddressResponse;
import com.supermall.backend.domain.user.entity.UserAddress;
import com.supermall.backend.domain.user.mapper.UserAddressMapper;
import com.supermall.backend.domain.user.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        // 如果是默认地址，先将其他地址设为非默认
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            update(new LambdaUpdateWrapper<UserAddress>()
                    .eq(UserAddress::getUserId, userId)
                    .eq(UserAddress::getIsDefault, true)
                    .set(UserAddress::getIsDefault, false));
        }

        UserAddress address = new UserAddress();
        BeanUtils.copyProperties(request, address);
        address.setUserId(userId);

        save(address);
        return convertToResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        UserAddress address = getOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId));

        if (address == null) {
            throw new BusinessException("地址不存在");
        }

        // 如果设置为默认地址，先将其他地址设为非默认
        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            update(new LambdaUpdateWrapper<UserAddress>()
                    .eq(UserAddress::getUserId, userId)
                    .eq(UserAddress::getIsDefault, true)
                    .set(UserAddress::getIsDefault, false));
        }

        BeanUtils.copyProperties(request, address);
        updateById(address);
        return convertToResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        remove(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId));
    }

    @Override
    public AddressResponse getAddress(Long userId, Long addressId) {
        UserAddress address = getOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId));

        if (address == null) {
            throw new BusinessException("地址不存在");
        }

        return convertToResponse(address);
    }

    @Override
    public List<AddressResponse> listAddresses(Long userId) {
        List<UserAddress> addresses = list(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreatedAt));

        return addresses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        // 先将所有地址设为非默认
        update(new LambdaUpdateWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, true)
                .set(UserAddress::getIsDefault, false));

        // 将指定地址设为默认
        update(new LambdaUpdateWrapper<UserAddress>()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId)
                .set(UserAddress::getIsDefault, true));
    }

    @Override
    public AddressResponse getDefaultAddress(Long userId) {
        UserAddress address = getOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, true));

        if (address == null) {
            throw new BusinessException("默认地址不存在");
        }

        return convertToResponse(address);
    }

    private AddressResponse convertToResponse(UserAddress address) {
        AddressResponse response = new AddressResponse();
        BeanUtils.copyProperties(address, response);
        return response;
    }
} 