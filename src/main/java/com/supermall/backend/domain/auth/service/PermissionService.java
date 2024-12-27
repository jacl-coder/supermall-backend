package com.supermall.backend.domain.auth.service;

import com.supermall.backend.domain.auth.dto.PermissionResponse;
import com.supermall.backend.domain.auth.entity.Permission;
import java.util.List;

public interface PermissionService {
    List<PermissionResponse> getAllPermissions();
    List<PermissionResponse> getPermissionsByRoleId(Integer roleId);
    List<Permission> getPermissionsByRoleIds(List<Integer> roleIds);
} 