package com.supermall.backend.domain.brand.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("brand")
@Schema(description = "品牌实体")
public class Brand {
    @TableId(type = IdType.AUTO)
    @Schema(description = "品牌ID")
    private Long id;

    @Schema(description = "品牌名称")
    private String name;

    @Schema(description = "品牌logo")
    private String logo;

    @Schema(description = "品牌描述")
    private String description;

    @Schema(description = "排序")
    private Integer sort;

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