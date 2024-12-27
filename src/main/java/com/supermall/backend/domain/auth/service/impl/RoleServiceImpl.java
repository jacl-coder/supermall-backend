package com.supermall.backend.domain.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.auth.dto.RoleRequest;
import com.supermall.backend.domain.auth.dto.RoleResponse;
import com.supermall.backend.domain.auth.dto.PermissionResponse;
import com.supermall.backend.domain.auth.entity.Role;
import com.supermall.backend.domain.auth.entity.RolePermission;
import com.supermall.backend.domain.auth.mapper.RoleMapper;
import com.supermall.backend.domain.auth.mapper.RolePermissionMapper;
import com.supermall.backend.domain.auth.service.PermissionService;
import com.supermall.backend.domain.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionService permissionService;

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        // 检查角色名称是否已存在
        if (exists(new LambdaQueryWrapper<Role>().eq(Role::getName, request.getName()))) {
            throw new BusinessException("角色名称已存在");
        }

        Role role = new Role();
        BeanUtils.copyProperties(request, role);
        save(role);

        // 分配权限
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            assignPermissions(role.getId(), request.getPermissionIds());
        }

        return getRoleResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long roleId, RoleRequest request) {
        Role role = getById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查角色名称是否与其他角色冲突
        if (!role.getName().equals(request.getName()) &&
                exists(new LambdaQueryWrapper<Role>().eq(Role::getName, request.getName()))) {
            throw new BusinessException("角色名称已存在");
        }

        BeanUtils.copyProperties(request, role);
        updateById(role);

        // 更新权限
        if (request.getPermissionIds() != null) {
            // 删除原有权限
            rolePermissionMapper.delete(
                    new LambdaQueryWrapper<RolePermission>()
                            .eq(RolePermission::getRoleId, roleId)
            );
            // 分配新权限
            assignPermissions(roleId, request.getPermissionIds());
        }

        return getRoleResponse(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        // 删除角色权限关联
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, roleId)
        );
        // 删除角色
        removeById(roleId);
    }

    @Override
    public RoleResponse getRole(Long roleId) {
        Role role = getById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return getRoleResponse(role);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return list().stream()
                .map(this::getRoleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除原有权限
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, roleId)
        );

        // 批量插入新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<RolePermission> rolePermissions = permissionIds.stream()
                    .map(permissionId -> {
                        RolePermission rp = new RolePermission();
                        rp.setRoleId(roleId);
                        rp.setPermissionId(permissionId);
                        return rp;
                    })
                    .collect(Collectors.toList());
            
            // 使用 MyBatis-Plus 的 saveBatch 方法替代 insertBatch
            rolePermissionMapper.insertBatchSomeColumn(rolePermissions);
        }
    }

    @Override
    public List<String> getRolePermissionCodes(Long roleId) {
        return permissionService.getPermissionsByRoleId(roleId).stream()
                .map(PermissionResponse::getName)
                .collect(Collectors.toList());
    }

    private RoleResponse getRoleResponse(Role role) {
        RoleResponse response = new RoleResponse();
        BeanUtils.copyProperties(role, response);
        response.setPermissions(permissionService.getPermissionsByRoleId(role.getId()));
        return response;
    }
} 