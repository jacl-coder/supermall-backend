package com.supermall.backend.domain.log.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.Result;
import com.supermall.backend.domain.log.entity.SystemLog;
import com.supermall.backend.domain.log.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/logs")
@RequiredArgsConstructor
public class SystemLogController {

    private final SystemLogService systemLogService;

    @GetMapping("/user/{authId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<SystemLog>> getUserLogs(
            @PathVariable Long authId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(systemLogService.getUserLogs(authId, page, size));
    }

    @GetMapping("/module/{module}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<SystemLog>> getModuleLogs(
            @PathVariable String module,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(systemLogService.getModuleLogs(module, page, size));
    }
} 