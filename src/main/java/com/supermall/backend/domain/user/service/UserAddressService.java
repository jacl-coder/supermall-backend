package com.supermall.backend.domain.user.service;

import com.supermall.backend.domain.user.dto.AddressRequest;
import com.supermall.backend.domain.user.dto.AddressResponse;
import java.util.List;

public interface UserAddressService {
    AddressResponse createAddress(Long userId, AddressRequest request);
    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);
    void deleteAddress(Long userId, Long addressId);
    AddressResponse getAddress(Long userId, Long addressId);
    List<AddressResponse> listAddresses(Long userId);
    void setDefaultAddress(Long userId, Long addressId);
    AddressResponse getDefaultAddress(Long userId);
} 