package com.supermall.backend.domain.user.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.user.dto.AddressRequest;
import com.supermall.backend.domain.user.dto.AddressResponse;
import com.supermall.backend.domain.user.service.UserAddressService;
import com.supermall.backend.common.security.model.SecurityUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public Result<AddressResponse> createAddress(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(addressService.createAddress(user.getId(), request));
    }

    @PutMapping("/{addressId}")
    public Result<AddressResponse> updateAddress(
            @PathVariable Integer addressId,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(addressService.updateAddress(user.getId(), addressId, request));
    }

    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(
            @PathVariable Integer addressId,
            @AuthenticationPrincipal SecurityUser user) {
        addressService.deleteAddress(user.getId(), addressId);
        return Result.success();
    }

    @GetMapping("/{addressId}")
    public Result<AddressResponse> getAddress(
            @PathVariable Integer addressId,
            @AuthenticationPrincipal SecurityUser user) {
        return Result.success(addressService.getAddress(user.getId(), addressId));
    }

    @GetMapping
    public Result<List<AddressResponse>> listAddresses(@AuthenticationPrincipal SecurityUser user) {
        return Result.success(addressService.listAddresses(user.getId()));
    }

    @PutMapping("/{addressId}/default")
    public Result<Void> setDefaultAddress(
            @PathVariable Integer addressId,
            @AuthenticationPrincipal SecurityUser user) {
        addressService.setDefaultAddress(user.getId(), addressId);
        return Result.success();
    }

    @GetMapping("/default")
    public Result<AddressResponse> getDefaultAddress(@AuthenticationPrincipal SecurityUser user) {
        return Result.success(addressService.getDefaultAddress(user.getId()));
    }
} 