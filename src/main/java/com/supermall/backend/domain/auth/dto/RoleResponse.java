package com.supermall.backend.domain.auth.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleResponse {
    private Integer id;
    private String name;
    private String description;
    private List<PermissionResponse> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 