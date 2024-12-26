package com.supermall.backend.domain.category.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private Integer sort;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String parentName;
    private List<CategoryVO> children;
} 