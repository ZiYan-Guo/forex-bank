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

DROP TABLE IF EXISTS t_risk_param_config;
CREATE TABLE IF NOT EXISTS t_risk_param_config (
    id BIGINT NOT NULL,
    param_key VARCHAR(50) NOT NULL COMMENT '参数键',
    param_value VARCHAR(500) NOT NULL COMMENT '参数值',
    param_type VARCHAR(30) NOT NULL COMMENT 'POSITION_LIMIT/STOP_LOSS/MARGIN_CALL/ALERT_THRESHOLD',
    currency VARCHAR(10) DEFAULT NULL COMMENT '适用币种',
    is_enabled TINYINT DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0 COMMENT '版本号',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_param_key_ccy (param_key, currency)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风控参数配置表';

-- ============================================
-- 资本项目便利化抽查规则表
-- Capital Account Facilitation Sampling Rules
-- ============================================
CREATE TABLE IF NOT EXISTS t_sampling_rule (
    id BIGINT NOT NULL, rule_code VARCHAR(50) NOT NULL COMMENT 'Rule Code 规则编码',
    rule_name VARCHAR(200) NOT NULL COMMENT 'Rule Name 规则名称',
    condition_json TEXT COMMENT 'Rule Condition JSON 规则条件',
    sampling_rate DECIMAL(5,2) NOT NULL DEFAULT 10 COMMENT 'Sampling Rate % 抽查比例',
    target_module VARCHAR(30) COMMENT 'Target Business Module 目标业务模块',
    effective_date DATE COMMENT '生效日期', expire_date DATE COMMENT '失效日期',
    priority INT DEFAULT 0, status VARCHAR(20) DEFAULT 'ACTIVE',
    is_auto_extract TINYINT DEFAULT 1 COMMENT '是否自动提取样本',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME, version INT DEFAULT 0, deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id), UNIQUE KEY uk_rule_code (rule_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Capital Account Facilitation Sampling Rules 资本项目便利化抽查规则表';

-- ============================================
-- 资本项目便利化抽查任务表
-- Capital Account Facilitation Sampling Tasks
-- ============================================
CREATE TABLE IF NOT EXISTS t_sampling_task (
    id BIGINT NOT NULL COMMENT 'Primary Key 主键ID',
    task_id VARCHAR(80) NOT NULL COMMENT 'Task ID 任务编号',
    biz_no VARCHAR(64) NOT NULL COMMENT 'Business No 业务编号',
    biz_type VARCHAR(30) NOT NULL COMMENT 'Business Type 业务类型',
    customer_id BIGINT COMMENT 'Customer ID 客户ID',
    amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT 'Transaction Amount 交易金额',
    currency VARCHAR(10) COMMENT 'Currency 币种',
    country_code VARCHAR(10) COMMENT 'Country Code 国家代码',
    account_age_days INT COMMENT 'Account Age Days 开户天数',
    sampling_rate DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT 'Sampling Rate 抽查比例',
    reason VARCHAR(500) COMMENT 'Sampling Reason 抽查原因',
    matched_rules_json TEXT COMMENT 'Matched Rule Codes JSON 命中规则编码JSON',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/COMPLETED 待处理/已完成',
    business_date DATE NOT NULL COMMENT 'Business Date 业务日期',
    completed_at DATETIME COMMENT 'Completed Time 完成时间',
    review_result VARCHAR(30) COMMENT 'Review Result 检查结果',
    review_comment VARCHAR(500) COMMENT 'Review Comment 检查意见',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 0,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_task_id (task_id),
    KEY idx_biz_no (biz_no),
    KEY idx_status (status),
    KEY idx_business_date (business_date),
    KEY idx_biz_type (biz_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Capital Account Facilitation Sampling Tasks 资本项目便利化抽查任务表';

-- Seed sampling rules 种子抽查规则
INSERT INTO t_sampling_rule (id, rule_code, rule_name, condition_json, sampling_rate, target_module, effective_date, status) VALUES
(1, 'SMP_HIGH_AMT', 'High Amount Transaction 大额交易', '{"minAmount":500000,"currency":"USD"}', 50.00, 'FX_PAYMENT', '2026-01-01', 'ACTIVE'),
(2, 'SMP_HIGH_RISK', 'High Risk Country 高风险国家', '{"countries":["IR","KP","MM"]}', 100.00, 'FX_PAYMENT', '2026-01-01', 'ACTIVE'),
(3, 'SMP_NEW_CUSTOMER', 'New Customer Transaction 新客户交易', '{"maxAccountAge":30}', 30.00, 'FX_EXCHANGE', '2026-01-01', 'ACTIVE');

-- Seed data
INSERT INTO t_risk_param_config (id, param_key, param_value, param_type, currency, is_enabled) VALUES
(1, 'position.limit.overall', '10000000', 'POSITION_LIMIT', 'USD', 1),
(2, 'position.limit.intraday', '5000000', 'POSITION_LIMIT', 'USD', 1),
(3, 'position.limit.overnight', '2000000', 'POSITION_LIMIT', 'USD', 1),
(4, 'stop.loss.total', '500000', 'STOP_LOSS', NULL, 1),
(5, 'margin.call.pct', '50', 'MARGIN_CALL', NULL, 1),
(6, 'margin.force.pct', '30', 'MARGIN_CALL', NULL, 1),
(7, 'alert.deviation.pct', '5', 'ALERT_THRESHOLD', NULL, 1);
