package com.supermall.backend.domain.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.user.entity.User;
import com.supermall.backend.domain.user.mapper.UserMapper;
import com.supermall.backend.domain.user.service.UserService;
import com.supermall.backend.security.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public User getByUsername(String username) {
        log.info("正在查询用户: {}", username);
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
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
        log.info("用户 {} 正在登录", username);
        User user = getByUsername(username);
        if (user == null) {
            log.warn("用户 {} 不存在", username);
            throw new ApiException("用户名或密码错误");
        }
        
        log.info("用户 {} 存在, 角色: {}, 状态: {}", username, user.getRole(), user.getStatus());
        log.info("数据库中的密码: {}", user.getPassword());
        log.info("输入的密码: {}", password);
        
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        log.info("密码匹配结果: {}", matches);
        
        if (!matches) {
            log.warn("用户 {} 密码错误", username);
            throw new ApiException("用户名或密码错误");
        }
        
        UserDetails userDetails = createUserDetails(user);
        log.info("创建的UserDetails: {}", userDetails);
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        try {
            String token = jwtTokenUtil.generateToken(userDetails);
            log.info("用户 {} 登录成功, 生成的token: {}", username, token);
            return token;
        } catch (Exception e) {
            log.error("生成token时发生错误", e);
            throw new ApiException("系统错误");
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