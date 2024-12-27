package com.supermall.backend.common.security.aspect;

import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.common.security.annotation.RequirePermission;
import com.supermall.backend.common.security.model.SecurityUser;
import com.supermall.backend.domain.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final RoleService roleService;

    @Before("@annotation(requirePermission)")
    public void checkPermission(RequirePermission requirePermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("未登录");
        }

        Long roleId = getRoleIdFromAuthentication(authentication);
        if (roleId == null) {
            throw new BusinessException("用户角色信息不存在");
        }

        List<String> permissions = roleService.getRolePermissionCodes(roleId);
        if (!permissions.contains(requirePermission.value())) {
            throw new BusinessException("没有操作权限");
        }
    }

    private Long getRoleIdFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof SecurityUser) {
            return ((SecurityUser) authentication.getPrincipal()).getRoleId();
        }
        return null;
    }
} 