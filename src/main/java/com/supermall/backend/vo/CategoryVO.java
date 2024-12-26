package com.supermall.backend.vo;

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
    private String icon;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<CategoryVO> children;
} 