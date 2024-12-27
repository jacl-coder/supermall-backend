package com.supermall.backend.domain.user.service;

import com.supermall.backend.domain.user.vo.UserVO;
import com.supermall.backend.domain.user.entity.User;

public interface UserService {
    /**
     * 根据用户名获取用户
     */
    User getByUsername(String username);

    /**
     * 注册用户
     */
    UserVO register(User user, String roleCode);

    /**
     * 用户登录
     */
    String login(String username, String password);

    /**
     * 刷新token
     */
    String refreshToken(String token);

    /**
     * 获取当前登录用户
     */
    User getCurrentUser();
} 