package com.supermall.backend.domain.auth.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.auth.dto.PermissionResponse;
import com.supermall.backend.domain.auth.service.PermissionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "权限管理", description = "包括权限的查询、分配、修改和删除等功能")
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