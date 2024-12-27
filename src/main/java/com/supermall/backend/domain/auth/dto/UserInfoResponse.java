package com.supermall.backend.domain.auth.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private String role;
    private List<String> permissions;
    private LocalDateTime lastLogin;
} 