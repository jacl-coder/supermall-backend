package com.supermall.backend.domain.user.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String phone;
    private List<String> roles;
    private LocalDateTime createdTime;
} 