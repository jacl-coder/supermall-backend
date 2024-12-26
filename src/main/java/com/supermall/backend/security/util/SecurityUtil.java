package com.supermall.backend.security.util;

import com.supermall.backend.common.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {
    
    private static final Logger log = LoggerFactory.getLogger(SecurityUtil.class);
    
    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.debug("当前认证信息: {}", authentication);
            
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new ApiException("用户未登录");
            }
            
            Object principal = authentication.getPrincipal();
            log.debug("当前用户主体: {}", principal);
            
            if (principal instanceof UserDetails) {
                return Long.valueOf(((UserDetails) principal).getUsername());
            }
            
            throw new ApiException("获取用户信息失败");
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