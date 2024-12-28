package com.supermall.backend.domain.log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.domain.log.entity.SystemLog;

public interface SystemLogService {
    /**
     * 记录系统日志
     */
    void log(Integer authId, String module, String action, String detail, String ipAddress, String userAgent);
    
    /**
     * 获取用户的操作日志
     */
    Page<SystemLog> getUserLogs(Integer authId, int page, int size);
    
    /**
     * 获取指定模块的操作日志
     */
    Page<SystemLog> getModuleLogs(String module, int page, int size);
} 