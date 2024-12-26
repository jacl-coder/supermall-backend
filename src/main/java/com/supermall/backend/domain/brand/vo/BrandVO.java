package com.supermall.backend.domain.brand.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BrandVO {
    private Long id;
    private String name;
    private String logo;
    private String description;
    private Integer status;
    private Integer sort;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
} 