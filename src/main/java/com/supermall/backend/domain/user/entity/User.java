package com.supermall.backend.domain.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;

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

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "性别：0-未知，1-男，2-女")
    private Integer gender;

    @Schema(description = "生日")
    private LocalDateTime birthday;

    @TableField("member_level")
    @Schema(description = "会员等级")
    private Integer memberLevel;

    @Schema(description = "积分")
    private Integer points;

    @Schema(description = "余额")
    private BigDecimal balance;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "角色：ADMIN-管理员，USER-普通用户")
    private String role;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "是否删除：0-未删除，1-已删除")
    private Integer deleted;
} 