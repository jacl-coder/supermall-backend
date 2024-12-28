package com.supermall.backend.domain.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.domain.log.entity.SystemLog;
import com.supermall.backend.domain.log.mapper.SystemLogMapper;
import com.supermall.backend.domain.log.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog> implements SystemLogService {

    @Override
    public void log(Integer authId, String module, String action, String detail, String ipAddress, String userAgent) {
        SystemLog log = new SystemLog();
        log.setAuthId(authId);
        log.setModule(module);
        log.setAction(action);
        log.setDetail(detail);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        
        save(log);
    }

    @Override
    public Page<SystemLog> getUserLogs(Integer authId, int page, int size) {
        return page(new Page<>(page, size),
                new LambdaQueryWrapper<SystemLog>()
                        .eq(SystemLog::getAuthId, authId)
                        .orderByDesc(SystemLog::getCreatedAt));
    }

    @Override
    public Page<SystemLog> getModuleLogs(String module, int page, int size) {
        return page(new Page<>(page, size),
                new LambdaQueryWrapper<SystemLog>()
                        .eq(SystemLog::getModule, module)
                        .orderByDesc(SystemLog::getCreatedAt));
    }
} 