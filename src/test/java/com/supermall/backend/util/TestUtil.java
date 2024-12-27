package com.supermall.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermall.backend.security.util.JwtTokenUtil;
import org.springframework.security.core.userdetails.UserDetails;

public class TestUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String createTestToken(JwtTokenUtil jwtTokenUtil, UserDetails userDetails) {
        return jwtTokenUtil.generateToken(userDetails);
    }

    public static String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
} 