-- ============================================
-- 风险监测模块 - 数据库初始化脚本
-- ============================================
USE forex_risk;

DROP TABLE IF EXISTS t_risk_monitor_log;
CREATE TABLE t_risk_monitor_log (
    id BIGINT NOT NULL COMMENT '主键ID',
    log_no VARCHAR(64) NOT NULL COMMENT '日志编号',
    customer_id BIGINT DEFAULT NULL COMMENT '客户ID',
    biz_type VARCHAR(30) NOT NULL COMMENT '业务类型',
    biz_no VARCHAR(64) NOT NULL COMMENT '业务编号',
    transaction_amount DECIMAL(22,6) NOT NULL COMMENT '交易金额',
    transaction_currency VARCHAR(10) NOT NULL COMMENT '交易币种',
    transaction_time DATETIME NOT NULL COMMENT '交易时间',
    monitor_rule_code VARCHAR(50) NOT NULL COMMENT '触发的监测规则编码',
    monitor_rule_name VARCHAR(100) NOT NULL COMMENT '监测规则名称',
    risk_category VARCHAR(30) NOT NULL COMMENT '风险类别: AML=反洗钱 SANCTION=制裁 SUSPICIOUS=可疑交易 LARGE_AMOUNT=大额交易 FREQUENT=频繁交易',
    risk_level VARCHAR(10) NOT NULL DEFAULT 'LOW' COMMENT '风险等级: LOW/MEDIUM/HIGH/CRITICAL',
    risk_score DECIMAL(10,2) DEFAULT 0 COMMENT '风险评分',
    check_result VARCHAR(30) NOT NULL COMMENT '检查结果: PASS=通过 REJECT=拒绝 MANUAL=人工审核 ESCALATE=升级',
    operator_id BIGINT DEFAULT NULL COMMENT '处理人ID',
    handle_time DATETIME DEFAULT NULL COMMENT '处理时间',
    handle_remark VARCHAR(500) DEFAULT NULL COMMENT '处理意见',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_log_no (log_no),
    KEY idx_customer_id (customer_id),
    KEY idx_biz_no (biz_no),
    KEY idx_transaction_time (transaction_time),
    KEY idx_risk_level (risk_level),
    KEY idx_check_result (check_result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险监测日志表';

DROP TABLE IF EXISTS t_monitor_rule;
CREATE TABLE t_monitor_rule (
    id BIGINT NOT NULL COMMENT '主键ID',
    rule_code VARCHAR(50) NOT NULL COMMENT '规则编码',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_type VARCHAR(30) NOT NULL COMMENT '规则类型: THRESHOLD=阈值 PATTERN=模式 BLACKLIST=黑名单 FREQUENCY=频率',
    risk_category VARCHAR(30) NOT NULL COMMENT '风险类别',
    rule_condition TEXT NOT NULL COMMENT '规则条件(JSON/Drools)',
    rule_action VARCHAR(30) NOT NULL COMMENT '规则动作: PASS/REJECT/MANUAL/ESCALATE',
    priority INT DEFAULT 0 COMMENT '优先级',
    is_enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_rule_code (rule_code),
    KEY idx_enabled (is_enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监测规则表';

DROP TABLE IF EXISTS t_risk_report;
CREATE TABLE t_risk_report (
    id BIGINT NOT NULL COMMENT '主键ID',
    report_no VARCHAR(64) NOT NULL COMMENT '报告编号',
    report_type VARCHAR(30) NOT NULL COMMENT '报告类型: SUSPICIOUS=可疑交易 LARGE_AMOUNT=大额交易 AML=反洗钱',
    report_period VARCHAR(30) NOT NULL COMMENT '报告期间',
    customer_id BIGINT DEFAULT NULL COMMENT '客户ID',
    total_transactions INT DEFAULT 0 COMMENT '涉及交易笔数',
    total_amount DECIMAL(22,6) DEFAULT 0 COMMENT '涉及交易总金额',
    report_content TEXT COMMENT '报告内容',
    report_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT=草稿 SUBMITTED=已提交 ACCEPTED=已接收 REJECTED=已退回',
    submit_time DATETIME DEFAULT NULL COMMENT '提交时间',
    submitter_id BIGINT DEFAULT NULL COMMENT '提交人',
    regulatory_ref VARCHAR(64) DEFAULT NULL COMMENT '监管回执号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_report_no (report_no),
    KEY idx_customer_id (customer_id),
    KEY idx_report_status (report_status),
    KEY idx_submit_time (submit_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险报告表';
