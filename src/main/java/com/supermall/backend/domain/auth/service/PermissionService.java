package com.supermall.backend.domain.auth.service;

import com.supermall.backend.domain.auth.dto.PermissionResponse;
import com.supermall.backend.domain.auth.entity.Permission;
import java.util.List;

public interface PermissionService {
    List<PermissionResponse> getAllPermissions();
    List<PermissionResponse> getPermissionsByRoleId(Long roleId);
    List<Permission> getPermissionsByRoleIds(List<Long> roleIds);
} 