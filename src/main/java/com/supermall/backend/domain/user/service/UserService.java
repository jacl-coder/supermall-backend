package com.supermall.backend.domain.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermall.backend.domain.user.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    /**
     * 根据用户名获取用户
     */
    User getByUsername(String username);

    /**
     * 注册用户
     */
    void register(String username, String password, String email);

    /**
     * 登录
     */
    String login(String username, String password);

    /**
     * 刷新token
     */
    String refreshToken(String token);
} 