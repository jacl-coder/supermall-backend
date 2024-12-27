package com.supermall.backend.domain.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.domain.auth.dto.PermissionResponse;
import com.supermall.backend.domain.auth.entity.Permission;
import com.supermall.backend.domain.auth.entity.RolePermission;
import com.supermall.backend.domain.auth.mapper.PermissionMapper;
import com.supermall.backend.domain.auth.mapper.RolePermissionMapper;
import com.supermall.backend.domain.auth.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public List<PermissionResponse> getAllPermissions() {
        return list().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionResponse> getPermissionsByRoleId(Long roleId) {
        List<Long> permissionIds = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, roleId)
        ).stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());

        if (permissionIds.isEmpty()) {
            return List.of();
        }

        return listByIds(permissionIds).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> getPermissionsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        List<Long> permissionIds = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>()
                        .in(RolePermission::getRoleId, roleIds)
        ).stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());

        if (permissionIds.isEmpty()) {
            return List.of();
        }

        return listByIds(permissionIds);
    }

    private PermissionResponse convertToResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        BeanUtils.copyProperties(permission, response);
        return response;
    }
} 