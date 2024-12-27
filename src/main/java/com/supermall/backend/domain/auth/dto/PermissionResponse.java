package com.supermall.backend.domain.auth.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PermissionResponse {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
} 