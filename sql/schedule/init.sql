-- ============================================
-- 定时任务模块 - 数据库初始化脚本
-- ============================================

DROP TABLE IF EXISTS t_schedule_job;
CREATE TABLE t_schedule_job (
    id BIGINT NOT NULL COMMENT '主键ID',
    job_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    job_group VARCHAR(50) NOT NULL COMMENT '任务组',
    job_handler VARCHAR(100) NOT NULL COMMENT '任务处理器',
    cron_expression VARCHAR(50) NOT NULL COMMENT 'Cron表达式',
    job_desc VARCHAR(500) DEFAULT NULL COMMENT '任务描述',
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLED' COMMENT '状态: ENABLED/DISABLED',
    last_result VARCHAR(1000) DEFAULT NULL COMMENT '最近执行结果',
    last_execute_time DATETIME DEFAULT NULL COMMENT '上次执行时间',
    next_execute_time DATETIME DEFAULT NULL COMMENT '下次执行时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号(乐观锁)',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_job_handler (job_handler),
    KEY idx_job_group (job_group),
    KEY idx_status (status),
    KEY idx_create_time (create_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务配置表';

DROP TABLE IF EXISTS t_job_log;
CREATE TABLE t_job_log (
    id BIGINT NOT NULL COMMENT '主键ID',
    job_id BIGINT NOT NULL COMMENT '任务ID',
    job_name VARCHAR(100) DEFAULT NULL COMMENT '任务名称',
    job_handler VARCHAR(100) DEFAULT NULL COMMENT '任务处理器',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME DEFAULT NULL COMMENT '结束时间',
    execute_status VARCHAR(20) NOT NULL DEFAULT 'RUNNING' COMMENT '执行状态: RUNNING/SUCCESS/FAILED',
    execute_result TEXT DEFAULT NULL COMMENT '执行结果',
    error_msg VARCHAR(2000) DEFAULT NULL COMMENT '错误信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_job_id (job_id),
    KEY idx_execute_status (execute_status),
    KEY idx_start_time (start_time DESC),
    KEY idx_create_time (create_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务执行日志表';

-- 默认任务种子数据
INSERT INTO t_schedule_job (id, job_name, job_group, job_handler, cron_expression, job_desc, status, create_time, update_time, version, deleted) VALUES
(1, '日终结算', 'DAILY', 'dailyClosingJob', '0 0 18 * * ?', '每日18:00执行日终结算', 'ENABLED', NOW(), NOW(), 0, 0),
(2, '对账任务', 'BATCH', 'reconciliationJob', '0 0 6 * * ?', '每日06:00执行对账', 'ENABLED', NOW(), NOW(), 0, 0),
(3, '批量报送', 'BATCH', 'reportingBatchJob', '0 0 10 * * ?', '每日10:00批量报送监管数据', 'ENABLED', NOW(), NOW(), 0, 0),
(4, '汇率刷新', 'MARKET', 'rateRefreshJob', '0 */5 * * * ?', '每5分钟刷新汇率', 'ENABLED', NOW(), NOW(), 0, 0);
