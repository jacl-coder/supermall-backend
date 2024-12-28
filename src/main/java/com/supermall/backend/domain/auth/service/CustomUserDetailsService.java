package com.supermall.backend.domain.auth.service;

import com.supermall.backend.domain.auth.entity.AuthUser;
import com.supermall.backend.common.security.model.SecurityUser;
import com.supermall.backend.domain.merchant.service.MerchantProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthUserService authUserService;
    private final RoleService roleService;
    private final MerchantProfileService merchantProfileService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 使用 getByUsername 方法查询用户
        AuthUser authUser = authUserService.getByUsername(username);
        if (authUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 获取用户权限
        List<String> permissions = roleService.getRolePermissionCodes(authUser.getRoleId());
        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        // 创建 SecurityUser 对象
        SecurityUser securityUser = new SecurityUser();
        securityUser.setId(authUser.getId());
        securityUser.setUsername(authUser.getUsername());
        securityUser.setPassword(authUser.getPasswordHash());
        securityUser.setRole("ROLE_" + roleService.getRole(authUser.getRoleId()).getName().toUpperCase());
        securityUser.setEnabled("ACTIVE".equalsIgnoreCase(authUser.getStatus()));
        securityUser.setAccountNonLocked(!"LOCKED".equalsIgnoreCase(authUser.getStatus()));

        // 如果是商家角色，设置商家ID
        if ("MERCHANT".equals(roleService.getRole(authUser.getRoleId()).getName())) {
            try {
                var merchant = merchantProfileService.getByAuthId(authUser.getId());
                securityUser.setMerchantId(merchant.getId());
            } catch (Exception e) {
                // 如果商家信息不存在或未审核通过，不设置商家ID
            }
        }

        return securityUser;
    }
} 