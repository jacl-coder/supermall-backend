package com.supermall.backend.domain.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.role.entity.Role;
import com.supermall.backend.domain.role.mapper.RoleMapper;
import com.supermall.backend.domain.user.entity.User;
import com.supermall.backend.domain.user.mapper.UserMapper;
import com.supermall.backend.domain.user.service.UserService;
import com.supermall.backend.domain.user.vo.UserVO;
import com.supermall.backend.security.model.CustomUserDetails;
import com.supermall.backend.security.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> 
        implements UserService, UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public User getByUsername(String username) {
        log.info("正在查询用户: {}", username);
        return userMapper.selectByUsername(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(User user, String roleCode) {
        // 1. 检查用户名是否已存在
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new ApiException("用户名已存在");
        }
        
        // 2. 获取角色
        Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getCode, roleCode));
        if (role == null) {
            throw new ApiException("角色不存在");
        }
        
        // 3. 保存用户
        user.setStatus(1);
        userMapper.insert(user);
        
        // 4. 保存用户角色关系
        userMapper.insertUserRole(user.getId(), role.getId());
        
        // 5. 返回用户信息
        return convertToVO(user);
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        
        // 设置角色列表
        List<Role> roles = roleMapper.selectByUserId(user.getId());
        vo.setRoles(roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toList()));
                
        return vo;
    }

    @Override
    public String login(String username, String password) {
        log.info("开始登录程 - 用户名: {}", username);
        try {
            User user = userMapper.selectByUsername(username);
            log.debug("SQL查询: SELECT id, username, password, avatar, phone, email, status, " +
                    "created_time, updated_time, deleted FROM user WHERE deleted = 0 AND username = '{}'", username);
            log.info("数据库查询结果 - 用户: {}", user);
            
            if (user == null) {
                log.warn("登录失败 - 用户不存在: {}", username);
                throw new ApiException("用户名或密码错误");
            }
            
            log.info("正在验证密码...");
            log.debug("数据库中的密码hash: {}", user.getPassword());
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            log.info("密码验证结果: {}", matches);
            
            if (!matches) {
                log.warn("登录失败 - 密码错误: {}", username);
                throw new ApiException("用户名或密码错误");
            }
            
            if (user.getStatus() == 0) {
                log.warn("账号已被禁用: {}", username);
                throw new ApiException("账号已被禁用");
            }
            
            try {
                // 创建用户详情
                CustomUserDetails userDetails = new CustomUserDetails();
                userDetails.setId(user.getId());
                userDetails.setUsername(user.getUsername());
                userDetails.setPassword(user.getPassword());
                userDetails.setEmail(user.getEmail());
                userDetails.setPhone(user.getPhone());
                userDetails.setAvatar(user.getAvatar());
                userDetails.setStatus(user.getStatus());
                
                // 设置用户角色
                List<Role> roles = roleMapper.selectByUserId(user.getId());
                List<String> roleCodes = roles.stream()
                        .map(Role::getCode)
                        .collect(Collectors.toList());
                userDetails.setAuthorities(roleCodes.stream()
                        .map(code -> new SimpleGrantedAuthority("ROLE_" + code))
                        .collect(Collectors.toList()));
                userDetails.setEnabled(user.getStatus() == 1);
                
                log.info("创建的用户详情: {}", userDetails);
                
                // 设置认证信息
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // 生成token
                String token = jwtTokenUtil.generateToken(userDetails);
                log.info("生成的token: {}", token);
                return token;
            } catch (Exception e) {
                log.error("登录过程中发生错误", e);
                throw new ApiException("系统异常，请联系管理员");
            }
        } catch (Exception e) {
            log.error("登录过程发生异常", e);
            throw new ApiException("登录失败: " + e.getMessage());
        }
    }

    @Override
    public String refreshToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new ApiException("token不能为空");
        }

        String username = jwtTokenUtil.getUsernameFromToken(token);
        if (username == null) {
            throw new ApiException("token已过期或无效");
        }

        UserDetails userDetails = loadUserByUsername(username);
        if (!jwtTokenUtil.validateToken(token, userDetails)) {
            throw new ApiException("token已过期或无效");
        }

        return jwtTokenUtil.generateToken(userDetails);
    }

    @Override
    public User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return getByUsername(userDetails.getUsername());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 创建用户详情
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setId(user.getId());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());
        userDetails.setEmail(user.getEmail());
        userDetails.setPhone(user.getPhone());
        userDetails.setAvatar(user.getAvatar());
        userDetails.setStatus(user.getStatus());
        
        // 设置用户角色
        List<Role> roles = roleMapper.selectByUserId(user.getId());
        List<String> roleCodes = roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toList());
        userDetails.setAuthorities(roleCodes.stream()
                .map(code -> new SimpleGrantedAuthority("ROLE_" + code))
                .collect(Collectors.toList()));
        userDetails.setEnabled(user.getStatus() == 1);

        return userDetails;
    }
} 