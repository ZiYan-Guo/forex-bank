-- ============================================
-- 工作流管理模块 - 数据库初始化脚本
-- ============================================
USE forex_workflow;

DROP TABLE IF EXISTS t_approval_record;
DROP TABLE IF EXISTS t_workflow_task;

CREATE TABLE t_workflow_task (
    id BIGINT NOT NULL COMMENT '主键ID',
    task_id VARCHAR(64) NOT NULL COMMENT '任务ID',
    biz_type VARCHAR(32) NOT NULL COMMENT '业务类型',
    biz_no VARCHAR(64) NOT NULL COMMENT '业务编号',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    assignee VARCHAR(64) DEFAULT NULL COMMENT '受理人',
    assignee_name VARCHAR(100) DEFAULT NULL COMMENT '受理人名称',
    process_definition_key VARCHAR(64) DEFAULT NULL COMMENT '流程定义Key',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/PROCESSING/APPROVED/REJECTED/CANCELLED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
    comment VARCHAR(1000) DEFAULT NULL COMMENT '备注',
    variables JSON DEFAULT NULL COMMENT '流程变量',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_task_id (task_id),
    KEY idx_biz_no (biz_no),
    KEY idx_assignee (assignee),
    KEY idx_status (status),
    KEY idx_biz_type (biz_type),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作流任务表';

CREATE TABLE t_approval_record (
    id BIGINT NOT NULL COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    step_name VARCHAR(100) NOT NULL COMMENT '审批步骤名称',
    approver VARCHAR(64) NOT NULL COMMENT '审批人',
    approver_name VARCHAR(100) DEFAULT NULL COMMENT '审批人名称',
    approve_result VARCHAR(20) NOT NULL COMMENT '审批结果: APPROVED/REJECTED',
    comment VARCHAR(1000) DEFAULT NULL COMMENT '审批意见',
    approve_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_approver (approver),
    KEY idx_approve_time (approve_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';
