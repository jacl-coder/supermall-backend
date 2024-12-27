package com.supermall.backend.domain.user.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.user.dto.AddressRequest;
import com.supermall.backend.domain.user.dto.AddressResponse;
import com.supermall.backend.domain.user.service.UserAddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户地址管理", description = "包括用户收货地址的增删改查、设置默认地址等功能")
@RestController
@RequestMapping("/api/users/addresses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserAddressController {

    private final UserAddressService addressService;

    @PostMapping
    public Result<AddressResponse> createAddress(@Valid @RequestBody AddressRequest request) {
        // TODO: 从SecurityContext获取userId
        Long userId = 1L;
        return Result.success(addressService.createAddress(userId, request));
    }

    @PutMapping("/{addressId}")
    public Result<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {
        Long userId = 1L;
        return Result.success(addressService.updateAddress(userId, addressId, request));
    }

    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(@PathVariable Long addressId) {
        Long userId = 1L;
        addressService.deleteAddress(userId, addressId);
        return Result.success();
    }

    @GetMapping("/{addressId}")
    public Result<AddressResponse> getAddress(@PathVariable Long addressId) {
        Long userId = 1L;
        return Result.success(addressService.getAddress(userId, addressId));
    }

    @GetMapping
    public Result<List<AddressResponse>> listAddresses() {
        Long userId = 1L;
        return Result.success(addressService.listAddresses(userId));
    }

    @PutMapping("/{addressId}/default")
    public Result<Void> setDefaultAddress(@PathVariable Long addressId) {
        Long userId = 1L;
        addressService.setDefaultAddress(userId, addressId);
        return Result.success();
    }

    @GetMapping("/default")
    public Result<AddressResponse> getDefaultAddress() {
        Long userId = 1L;
        return Result.success(addressService.getDefaultAddress(userId));
    }
} 