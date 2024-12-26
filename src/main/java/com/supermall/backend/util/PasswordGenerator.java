package com.supermall.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        String password = "123456";
        String encodedPassword = encoder.encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("加密后的密码: " + encodedPassword);
        System.out.println("验证结果: " + encoder.matches(password, encodedPassword));
    }
} 