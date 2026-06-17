-- Month-end closing table. 月末结账表.
CREATE TABLE IF NOT EXISTS `t_month_end_closing` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `closing_id`     VARCHAR(32)  NOT NULL COMMENT '结账编号',
    `fiscal_period`  VARCHAR(16)  NOT NULL COMMENT '会计期间 yyyyMM',
    `closing_date`   DATE         NOT NULL COMMENT '结账日期',
    `closing_status` VARCHAR(20)  NOT NULL DEFAULT 'OPEN' COMMENT '状态: OPEN/IN_PROGRESS/COMPLETED/LOCKED',
    `checklist_json` TEXT         COMMENT '结账检查清单 JSON',
    `audit_trail`    TEXT         COMMENT '审计跟踪',
    `total_debits`   DECIMAL(20,2) NOT NULL DEFAULT 0.00 COMMENT '借方总额',
    `total_credits`  DECIMAL(20,2) NOT NULL DEFAULT 0.00 COMMENT '贷方总额',
    `operator_id`    BIGINT       COMMENT '操作员 ID',
    `version`        INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
    `deleted`        TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_closing_id` (`closing_id`),
    KEY `idx_fiscal_period` (`fiscal_period`),
    KEY `idx_closing_status` (`closing_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月末结账表';
