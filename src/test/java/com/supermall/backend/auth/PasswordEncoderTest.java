package com.supermall.backend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testPasswordEncoding() {
        String rawPassword = "Admin123456";
        
        // 生成新的密码哈希
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("Generated password hash: " + encodedPassword);
        
        // 验证密码匹配
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        assertTrue(matches, "密码应该匹配");

        // 测试新密码加密是否每次生成不同的哈希
        String anotherEncodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("Another password hash: " + anotherEncodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, anotherEncodedPassword), "新加密的密码应该匹配");
        assertNotEquals(encodedPassword, anotherEncodedPassword, "每次加密应生成不同的哈希值");
    }
} 