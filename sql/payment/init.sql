-- ============================================
-- 跨境支付模块 - 数据库初始化脚本
-- ============================================
USE forex_payment;

DROP TABLE IF EXISTS t_cross_border_payment;
CREATE TABLE t_cross_border_payment (
    id BIGINT NOT NULL COMMENT '主键ID',
    payment_no VARCHAR(64) NOT NULL COMMENT '汇款编号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    payment_direction VARCHAR(10) NOT NULL COMMENT '方向: INWARD=汇入 OUTWARD=汇出',
    payment_type VARCHAR(30) NOT NULL COMMENT '汇款类型: TT=电汇 DD=票汇 CIPS=跨境人民币',
    pay_amount DECIMAL(22,6) NOT NULL COMMENT '汇款金额',
    pay_currency VARCHAR(10) NOT NULL COMMENT '汇款币种',
    settlement_amount DECIMAL(22,6) DEFAULT NULL COMMENT '结算金额(人民币)',
    exchange_rate DECIMAL(16,8) DEFAULT NULL COMMENT '折算汇率',
    sender_info JSON DEFAULT NULL COMMENT '汇款人信息(JSON)',
    beneficiary_info JSON NOT NULL COMMENT '收款人信息(JSON)',
    intermediary_bank_info JSON DEFAULT NULL COMMENT '中间行信息(JSON)',
    paying_bank_code VARCHAR(20) DEFAULT NULL COMMENT '付款行SWIFT CODE',
    receiving_bank_code VARCHAR(20) DEFAULT NULL COMMENT '收款行SWIFT CODE',
    message_type VARCHAR(20) DEFAULT NULL COMMENT '报文类型: MT103/MT202/ISO20022',
    swift_ref VARCHAR(64) DEFAULT NULL COMMENT 'SWIFT报文参考号',
    cips_ref VARCHAR(64) DEFAULT NULL COMMENT 'CIPS报文参考号',
    gpi_tracking_id VARCHAR(100) DEFAULT NULL COMMENT 'SWIFT GPI追踪ID',
    gpi_status VARCHAR(30) DEFAULT NULL COMMENT 'GPI状态',
    payment_purpose VARCHAR(500) DEFAULT NULL COMMENT '汇款用途/附言',
    bank_purpose_code VARCHAR(20) DEFAULT NULL COMMENT '银行用途代码',
    charge_bearer VARCHAR(10) DEFAULT 'SHA' COMMENT '费用承担: OUR/BEN/SHA',
    fee_amount DECIMAL(22,6) DEFAULT 0 COMMENT '手续费',
    telegraphic_fee DECIMAL(22,6) DEFAULT 0 COMMENT '电报费',
    commission_amount DECIMAL(22,6) DEFAULT 0 COMMENT '佣金',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT=草稿 SUBMITTED=已提交 AML_CHECK=反洗钱筛查 AML_PASSED=筛查通过 AML_REJECTED=筛查拒绝 SENT=已发送 ACK_RECEIVED=确认已收 FUNDS_CREDITED=资金已入账 RETURNED=退回 FAILED=失败 CANCELLED=已取消',
    submit_time DATETIME DEFAULT NULL COMMENT '提交时间',
    value_date DATE DEFAULT NULL COMMENT '起息日',
    settlement_date DATE DEFAULT NULL COMMENT '清算日期',
    operator_id BIGINT DEFAULT NULL COMMENT '操作员ID',
    approver_id BIGINT DEFAULT NULL COMMENT '审批人ID',
    remark VARCHAR(1000) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_payment_no (payment_no),
    KEY idx_customer_id (customer_id),
    KEY idx_payment_status (payment_status),
    KEY idx_submit_time (submit_time),
    KEY idx_value_date (value_date),
    KEY idx_swift_ref (swift_ref),
    KEY idx_cips_ref (cips_ref),
    KEY idx_customer_status (customer_id, payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跨境支付订单表';

DROP TABLE IF EXISTS t_payment_blacklist_hit;
CREATE TABLE t_payment_blacklist_hit (
    id BIGINT NOT NULL COMMENT '主键ID',
    payment_id BIGINT NOT NULL COMMENT '支付订单ID',
    payment_no VARCHAR(64) NOT NULL COMMENT '汇款编号',
    hit_type VARCHAR(30) NOT NULL COMMENT '命中类型: SANCTION=制裁名单 AML=反洗钱 PEP=政要人物',
    hit_list_name VARCHAR(200) NOT NULL COMMENT '命中的名单名称',
    hit_field VARCHAR(50) NOT NULL COMMENT '命中字段: NAME=名称 ADDRESS=地址 SWIFT_CODE=SWIFT码',
    hit_value VARCHAR(500) NOT NULL COMMENT '命中值',
    match_score DECIMAL(5,2) DEFAULT NULL COMMENT '匹配度',
    check_time DATETIME NOT NULL COMMENT '筛查时间',
    check_result VARCHAR(20) NOT NULL COMMENT '筛查结果: HIT=命中 CLEAR=放过 REVIEW=待人工审核',
    reviewer_id BIGINT DEFAULT NULL COMMENT '审核人ID',
    review_time DATETIME DEFAULT NULL COMMENT '审核时间',
    review_comment VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_payment_id (payment_id),
    KEY idx_hit_type (hit_type),
    KEY idx_check_result (check_result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付黑名单命中记录表';

DROP TABLE IF EXISTS t_payment_reconciliation;
CREATE TABLE t_payment_reconciliation (
    id BIGINT NOT NULL COMMENT '主键ID',
    payment_id BIGINT DEFAULT NULL COMMENT '支付订单ID',
    nostro_account VARCHAR(30) NOT NULL COMMENT '往来账户号',
    currency VARCHAR(10) NOT NULL COMMENT '币种',
    transaction_ref VARCHAR(100) NOT NULL COMMENT '交易参考号',
    statement_date DATE NOT NULL COMMENT '对账单日期',
    nostro_amount DECIMAL(22,6) NOT NULL COMMENT '往来账户金额',
    nostro_direction VARCHAR(10) NOT NULL COMMENT '借贷方向: DEBIT/CREDIT',
    system_amount DECIMAL(22,6) NOT NULL COMMENT '系统记录金额',
    system_direction VARCHAR(10) NOT NULL COMMENT '系统借贷方向',
    reconciliation_status VARCHAR(20) NOT NULL COMMENT '对账状态: MATCHED=匹配 UNMATCHED=未匹配 MANUAL_MATCH=人工匹配',
    match_time DATETIME DEFAULT NULL COMMENT '匹配时间',
    difference DECIMAL(22,6) DEFAULT 0 COMMENT '差异金额',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_statement_date (statement_date),
    KEY idx_reconciliation_status (reconciliation_status),
    KEY idx_transaction_ref (transaction_ref)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付对账表';

-- ============================================
-- 跨境支付场景模板表
-- Cross-border Payment Scenario Templates
-- ============================================
CREATE TABLE IF NOT EXISTS t_payment_template (
    id BIGINT NOT NULL, template_code VARCHAR(50) NOT NULL COMMENT 'Template Code 模板编码',
    template_name VARCHAR(200) NOT NULL COMMENT 'Template Name 模板名称',
    scenario_type VARCHAR(30) NOT NULL COMMENT 'Scenario Type 场景类型: STUDY_ABROAD/TRAVEL_DEPOSIT/MEDICAL_EXPENSE/CUSTOM',
    payment_direction VARCHAR(10) DEFAULT 'OUTWARD', default_pay_currency VARCHAR(10),
    default_beneficiary_country VARCHAR(10), beneficiary_details TEXT COMMENT 'Pre-filled Beneficiary JSON 预填受益人信息',
    default_purpose TEXT, default_purpose_code VARCHAR(20),
    usage_instructions TEXT, sort_order INT DEFAULT 0,
    is_public TINYINT DEFAULT 0, owner_customer_id BIGINT,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME, version INT DEFAULT 0, deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id), UNIQUE KEY uk_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Cross-border Payment Scenario Templates 跨境支付场景模板表';

-- Seed scenario templates 种子场景模板
INSERT INTO t_payment_template (id, template_code, template_name, scenario_type, default_pay_currency, default_purpose, is_public) VALUES
(1, 'TPL_STUDY_001', 'Study Abroad - US Tuition 美国留学学费', 'STUDY_ABROAD', 'USD', 'OVERSEAS STUDY TUITION PAYMENT', 1),
(2, 'TPL_TRAVEL_001', 'Travel Deposit - Thailand 泰国旅游保证金', 'TRAVEL_DEPOSIT', 'CNY', 'TRAVEL SECURITY DEPOSIT', 1),
(3, 'TPL_MEDICAL_001', 'Overseas Medical Expense 境外医疗费用', 'MEDICAL_EXPENSE', 'JPY', 'OVERSEAS MEDICAL TREATMENT EXPENSE', 1);

DROP TABLE IF EXISTS t_mt_mx_conversion_log;
CREATE TABLE IF NOT EXISTS t_mt_mx_conversion_log (
    id BIGINT NOT NULL, log_no VARCHAR(64) NOT NULL,
    source_type VARCHAR(20) NOT NULL COMMENT 'MT103/MT202',
    target_type VARCHAR(20) NOT NULL COMMENT 'pain.001/pacs.009',
    source_message TEXT, target_message TEXT,
    conversion_status VARCHAR(20) COMMENT 'SUCCESS/FAILED',
    error_reason VARCHAR(500), operator_id BIGINT,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id), UNIQUE KEY uk_log_no (log_no), KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MT↔MX转换日志表';
