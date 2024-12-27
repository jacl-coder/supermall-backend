package com.supermall.backend.domain.auth.service;

import com.supermall.backend.domain.auth.dto.RoleRequest;
import com.supermall.backend.domain.auth.dto.RoleResponse;
import java.util.List;

public interface RoleService {
    RoleResponse createRole(RoleRequest request);
    RoleResponse updateRole(Integer roleId, RoleRequest request);
    void deleteRole(Integer roleId);
    RoleResponse getRole(Integer roleId);
    List<RoleResponse> getAllRoles();
    void assignPermissions(Integer roleId, List<Integer> permissionIds);
    List<String> getRolePermissionCodes(Integer roleId);
} 