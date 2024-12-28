package com.supermall.backend.domain.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.log.entity.SystemLog;
import com.supermall.backend.domain.log.mapper.SystemLogMapper;
import com.supermall.backend.domain.log.service.SystemLogService;
import org.springframework.stereotype.Service;

@Service
public class SystemLogServiceImpl implements SystemLogService {
    private final SystemLogMapper systemLogMapper;

    public SystemLogServiceImpl(SystemLogMapper systemLogMapper) {
        this.systemLogMapper = systemLogMapper;
    }

    @Override
    public void log(Integer authId, String module, String action, String description, String ipAddress, String userAgent) {
        SystemLog log = new SystemLog();
        log.setAuthId(authId);
        log.setModule(module);
        log.setAction(action);
        log.setDescription(description);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        systemLogMapper.insert(log);
    }

    @Override
    public Page<SystemLog> getUserLogs(Integer authId, int page, int size) {
        Page<SystemLog> pageParam = new Page<>(page, size);
        QueryWrapper<SystemLog> wrapper = new QueryWrapper<>();
        wrapper.eq("auth_id", authId)
                .orderByDesc("created_at");
        return systemLogMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Page<SystemLog> getModuleLogs(String module, int page, int size) {
        Page<SystemLog> pageParam = new Page<>(page, size);
        QueryWrapper<SystemLog> wrapper = new QueryWrapper<>();
        wrapper.eq("module", module)
                .orderByDesc("created_at");
        return systemLogMapper.selectPage(pageParam, wrapper);
    }
} 