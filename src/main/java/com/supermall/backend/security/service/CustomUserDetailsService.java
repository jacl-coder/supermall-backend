package com.supermall.backend.security.service;

import com.supermall.backend.domain.role.entity.Role;
import com.supermall.backend.domain.role.mapper.RoleMapper;
import com.supermall.backend.domain.user.entity.User;
import com.supermall.backend.domain.user.service.UserService;
import com.supermall.backend.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户认证服务
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;
    private final RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setId(user.getId());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());
        userDetails.setEmail(user.getEmail());
        userDetails.setPhone(user.getPhone());
        userDetails.setAvatar(user.getAvatar());
        userDetails.setStatus(user.getStatus());
        userDetails.setEnabled(user.getStatus() == 1);

        // 设置用户角色
        List<Role> roles = roleMapper.selectByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getCode()))
                .collect(Collectors.toList());
        userDetails.setAuthorities(authorities);

        return userDetails;
    }
} 