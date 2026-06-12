package com.forex.common.audit;

/**
 * 审计日志服务接口
 * 各业务模块需要实现此接口存储审计日志
 */
public interface AuditLogService {
    
    /**
     * 保存审计日志
     */
    void save(AuditLogDto logDto);
    
    /**
     * 异步保存（推荐使用，不阻塞业务）
     */
    void saveAsync(AuditLogDto logDto);
}
