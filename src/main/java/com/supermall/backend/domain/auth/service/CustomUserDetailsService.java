package com.supermall.backend.domain.auth.service;

import com.supermall.backend.domain.auth.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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

        // 根据数据库表的字段构建 UserDetails
        return User.builder()
                .username(authUser.getUsername())
                .password(authUser.getPasswordHash())
                .disabled(!"active".equals(authUser.getStatus()))
                .accountLocked("locked".equals(authUser.getStatus()))
                .authorities(authorities)
                .build();
    }
} 