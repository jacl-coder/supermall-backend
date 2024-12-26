package com.supermall.backend.security.util;

import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.user.entity.User;
import com.supermall.backend.domain.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
    
    private static final Logger log = LoggerFactory.getLogger(SecurityUtil.class);
    private static UserService userService;
    
    public SecurityUtil(UserService userService) {
        SecurityUtil.userService = userService;
    }
    
    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        try {
            String username = getCurrentUsername();
            User user = userService.getByUsername(username);
            if (user == null) {
                throw new ApiException("用户不存在");
            }
            return user.getId();
        } catch (Exception e) {
            log.error("获取当前用户ID失败", e);
            throw new ApiException("获取当前用户ID失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApiException("用户未登录");
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        
        throw new ApiException("获取用户信息失败");
    }
    
    /**
     * 判断用户是否已登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
} 