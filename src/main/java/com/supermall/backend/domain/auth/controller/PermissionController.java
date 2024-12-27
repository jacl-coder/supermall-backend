package com.supermall.backend.domain.auth.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.auth.dto.PermissionResponse;
import com.supermall.backend.domain.auth.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<PermissionResponse>> getAllPermissions() {
        return Result.success(permissionService.getAllPermissions());
    }

    @GetMapping("/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<PermissionResponse>> getPermissionsByRoleId(@PathVariable Integer roleId) {
        return Result.success(permissionService.getPermissionsByRoleId(roleId));
    }
} 