package com.supermall.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密后的密码: " + encodedPassword);
        System.out.println("验证结果: " + encoder.matches(rawPassword, encodedPassword));
        
        // 验证数据库中的密码
        String dbPassword = "admin"; // 当前数据库中的密码
        System.out.println("当前数据库密码验证结果: " + encoder.matches(rawPassword, dbPassword));
    }
} 