package com.supermall.backend.domain.auth.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.auth.dto.RoleRequest;
import com.supermall.backend.domain.auth.dto.RoleResponse;
import com.supermall.backend.domain.auth.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        return Result.success(roleService.createRole(request));
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<RoleResponse> updateRole(
            @PathVariable Integer roleId,
            @Valid @RequestBody RoleRequest request) {
        return Result.success(roleService.updateRole(roleId, request));
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteRole(@PathVariable Integer roleId) {
        roleService.deleteRole(roleId);
        return Result.success();
    }

    @GetMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<RoleResponse> getRole(@PathVariable Integer roleId) {
        return Result.success(roleService.getRole(roleId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<RoleResponse>> getAllRoles() {
        return Result.success(roleService.getAllRoles());
    }

    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> assignPermissions(
            @PathVariable Integer roleId,
            @RequestBody List<Integer> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return Result.success();
    }
} 