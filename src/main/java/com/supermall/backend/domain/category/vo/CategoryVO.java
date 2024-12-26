package com.supermall.backend.domain.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商品分类VO
 */
@Data
@Schema(description = "商品分类VO")
public class CategoryVO {
    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父级分类ID")
    private Long parentId;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "子分类列表")
    private List<CategoryVO> children;
} 