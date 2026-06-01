-- ============================================
-- 通知公告模块 - 数据库初始化脚本
-- ============================================
USE forex_notification;

DROP TABLE IF EXISTS t_notice;
DROP TABLE IF EXISTS t_notification;

CREATE TABLE t_notification (
    id BIGINT NOT NULL COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    notify_type VARCHAR(20) NOT NULL COMMENT '通知类型: SMS/EMAIL/APP/SYSTEM',
    target_users VARCHAR(2000) DEFAULT NULL COMMENT '目标用户ID列表(JSON数组)',
    target_user_names VARCHAR(2000) DEFAULT NULL COMMENT '目标用户名称列表',
    biz_type VARCHAR(32) DEFAULT NULL COMMENT '业务类型',
    biz_no VARCHAR(64) DEFAULT NULL COMMENT '业务编号',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/SENT/FAILED',
    send_time DATETIME DEFAULT NULL COMMENT '发送时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    failed_reason VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_biz_no (biz_no),
    KEY idx_notify_type (notify_type),
    KEY idx_status (status),
    KEY idx_biz_type (biz_type),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知记录表';

CREATE TABLE t_notice (
    id BIGINT NOT NULL COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    notice_type VARCHAR(20) NOT NULL COMMENT '公告类型: ANNOUNCEMENT/ALERT/REMINDER',
    publish_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '发布状态: DRAFT/PUBLISHED/EXPIRED',
    publisher_id BIGINT DEFAULT NULL COMMENT '发布人ID',
    publish_time DATETIME DEFAULT NULL COMMENT '发布时间',
    expire_time DATETIME DEFAULT NULL COMMENT '过期时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_notice_type (notice_type),
    KEY idx_publish_status (publish_status),
    KEY idx_publish_time (publish_time),
    KEY idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';
