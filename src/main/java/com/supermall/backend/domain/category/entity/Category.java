package com.supermall.backend.domain.category.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类实体
 */
@Data
@TableName("category")
@Schema(description = "商品分类实体")
public class Category {
    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;

    @TableLogic
    @Schema(description = "是否删除：0-未删除，1-已删除")
    private Integer deleted;
} 