package com.supermall.backend.domain.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("user")
@Schema(description = "用户实体")
public class User {
    @TableId(type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "角色：ROLE_USER, ROLE_ADMIN")
    private String role;

    @TableField("created_time")
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @TableField("updated_time")
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;

    @TableLogic
    @Schema(description = "是否删除：0-未删除，1-已删除")
    private Integer deleted;
} 