package com.supermall.backend.domain.user.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.user.dto.AddressRequest;
import com.supermall.backend.domain.user.dto.AddressResponse;
import com.supermall.backend.domain.user.service.UserAddressService;
import com.supermall.backend.domain.user.service.UserProfileService;
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
    private final UserProfileService profileService;

    @PostMapping
    public Result<AddressResponse> createAddress(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        Integer profileId = profileService.getProfileByAuthId(user.getId()).getId();
        return Result.success(addressService.createAddress(profileId, request));
    }

    @PutMapping("/{addressId}")
    public Result<AddressResponse> updateAddress(
            @PathVariable Integer addressId,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        Integer profileId = profileService.getProfileByAuthId(user.getId()).getId();
        return Result.success(addressService.updateAddress(profileId, addressId, request));
    }

    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(
            @PathVariable Integer addressId,
            @AuthenticationPrincipal SecurityUser user) {
        Integer profileId = profileService.getProfileByAuthId(user.getId()).getId();
        addressService.deleteAddress(profileId, addressId);
        return Result.success();
    }

    @GetMapping("/{addressId}")
    public Result<AddressResponse> getAddress(
            @PathVariable Integer addressId,
            @AuthenticationPrincipal SecurityUser user) {
        Integer profileId = profileService.getProfileByAuthId(user.getId()).getId();
        return Result.success(addressService.getAddress(profileId, addressId));
    }

    @GetMapping
    public Result<List<AddressResponse>> listAddresses(@AuthenticationPrincipal SecurityUser user) {
        Integer profileId = profileService.getProfileByAuthId(user.getId()).getId();
        return Result.success(addressService.listAddresses(profileId));
    }

    @PutMapping("/{addressId}/default")
    public Result<Void> setDefaultAddress(
            @PathVariable Integer addressId,
            @AuthenticationPrincipal SecurityUser user) {
        Integer profileId = profileService.getProfileByAuthId(user.getId()).getId();
        addressService.setDefaultAddress(profileId, addressId);
        return Result.success();
    }

    @GetMapping("/default")
    public Result<AddressResponse> getDefaultAddress(@AuthenticationPrincipal SecurityUser user) {
        Integer profileId = profileService.getProfileByAuthId(user.getId()).getId();
        return Result.success(addressService.getDefaultAddress(profileId));
    }
} 