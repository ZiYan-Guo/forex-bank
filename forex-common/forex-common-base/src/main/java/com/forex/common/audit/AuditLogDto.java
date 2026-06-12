package com.forex.common.audit;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审计日志数据传输对象
 */
@Data
public class AuditLogDto {
    
    private Long userId;
    private String username;
    private String operationType;
    private String entityType;
    private String methodName;
    private String className;
    private String args;
    private String result;
    private String ipAddress;
    private Long duration;
    private String status;
    private String errorMessage;
    private LocalDateTime executedAt;
}
