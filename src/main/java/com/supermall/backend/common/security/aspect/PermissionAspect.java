package com.supermall.backend.common.security.aspect;

import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.common.security.annotation.RequirePermission;
import com.supermall.backend.common.security.model.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("未登录");
        }

        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        if (user == null) {
            throw new BusinessException("用户信息不存在");
        }

        // 管理员拥有所有权限
        if (user.isAdmin()) {
            return;
        }

        // 检查角色权限
        String requiredRole = requirePermission.role();
        if (!requiredRole.isEmpty()) {
            if ("MERCHANT".equals(requiredRole) && !user.isMerchant()) {
                throw new BusinessException("需要商家权限");
            }
            if ("USER".equals(requiredRole) && !user.isUser()) {
                throw new BusinessException("需要用户权限");
            }
        }

        // 检查商家权限
        if (requirePermission.requireMerchant() && !user.isMerchant()) {
            throw new BusinessException("需要商家权限");
        }

        // 检查商家ID
        if (requirePermission.requireMerchant() && user.getMerchantId() == null) {
            throw new BusinessException("商家信息不存在");
        }
    }
} 