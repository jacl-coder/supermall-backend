package com.supermall.backend.domain.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.user.entity.User;
import com.supermall.backend.domain.user.mapper.UserMapper;
import com.supermall.backend.domain.user.service.UserService;
import com.supermall.backend.security.model.CustomUserDetails;
import com.supermall.backend.security.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public User getByUsername(String username) {
        log.info("正在查询用户: {}", username);
        return userMapper.selectByUsername(username);
    }

    @Override
    @Transactional
    public void register(String username, String password, String email) {
        log.info("正在注册用户: {}, 邮箱: {}", username, email);
        
        // 验证用户名是否已存在
        long count = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (count > 0) {
            log.warn("用户名 {} 已存在", username);
            throw new ApiException("用户名已存在");
        }

        // 验证邮箱是否已存在
        count = count(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (count > 0) {
            log.warn("邮箱 {} 已存在", email);
            throw new ApiException("邮箱已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setStatus(1);
        user.setRole("USER");
        save(user);
        log.info("用户 {} 注册成功", username);
    }

    @Override
    public String login(String username, String password) {
        log.info("开始登录流程 - 用户名: {}", username);
        try {
            User user = userMapper.selectByUsername(username);
            log.debug("SQL查询: SELECT id, username, password, avatar, phone, email, status, role, " +
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
                userDetails.setRole(user.getRole() != null ? user.getRole() : "USER");
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
        log.info("正在刷新令牌: {}", token);
        if (!StringUtils.hasText(token)) {
            log.warn("令牌为空");
            throw new ApiException("token不能为空");
        }
        
        try {
            // 如果token以Bearer 开头,去掉前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            String username = jwtTokenUtil.extractUsername(token);
            log.info("从令牌中提取的用户名: {}", username);
            
            if (!StringUtils.hasText(username)) {
                log.warn("无法从令牌中提取用户名");
                throw new ApiException("token已过期或不合法");
            }
            
            User user = getByUsername(username);
            if (user == null) {
                log.warn("用户 {} 不存在", username);
                throw new ApiException("用户不存在");
            }
            
            log.info("找到用户: {}, 角色: {}", username, user.getRole());
            UserDetails userDetails = createUserDetails(user);
            
            if (jwtTokenUtil.validateToken(token, userDetails)) {
                String newToken = jwtTokenUtil.generateToken(userDetails);
                log.info("用户 {} 令牌刷新成功, 新令牌: {}", username, newToken);
                return newToken;
            }
            
            log.warn("令牌验证失败");
            throw new ApiException("token已过期或不合法");
        } catch (Exception e) {
            log.error("刷新令牌时发生错误", e);
            throw new ApiException(e.getMessage());
        }
    }

    private UserDetails createUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
} 