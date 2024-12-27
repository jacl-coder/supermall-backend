package com.supermall.backend.domain.auth.service;

import com.supermall.backend.domain.auth.dto.RoleRequest;
import com.supermall.backend.domain.auth.dto.RoleResponse;
import java.util.List;

public interface RoleService {
    RoleResponse createRole(RoleRequest request);
    RoleResponse updateRole(Long roleId, RoleRequest request);
    void deleteRole(Long roleId);
    RoleResponse getRole(Long roleId);
    List<RoleResponse> getAllRoles();
    void assignPermissions(Long roleId, List<Long> permissionIds);
    List<String> getRolePermissionCodes(Long roleId);
} 