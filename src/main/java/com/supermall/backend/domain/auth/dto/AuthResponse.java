package com.supermall.backend.domain.auth.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private String role;
    private String token;
} 