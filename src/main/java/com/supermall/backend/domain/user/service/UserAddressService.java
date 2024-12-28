package com.supermall.backend.domain.user.service;

import com.supermall.backend.domain.user.dto.AddressRequest;
import com.supermall.backend.domain.user.dto.AddressResponse;
import java.util.List;

public interface UserAddressService {
    AddressResponse createAddress(Integer userId, AddressRequest request);
    AddressResponse updateAddress(Integer userId, Integer addressId, AddressRequest request);
    void deleteAddress(Integer userId, Integer addressId);
    AddressResponse getAddress(Integer userId, Integer addressId);
    List<AddressResponse> listAddresses(Integer userId);
    void setDefaultAddress(Integer userId, Integer addressId);
    AddressResponse getDefaultAddress(Integer userId);
} 