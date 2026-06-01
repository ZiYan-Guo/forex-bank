-- ============================================
-- 监管报送模块 - 数据库初始化脚本
-- ============================================
USE forex_reporting;

DROP TABLE IF EXISTS t_balance_of_payment;
CREATE TABLE t_balance_of_payment (
    id BIGINT NOT NULL COMMENT '主键ID',
    report_no VARCHAR(64) NOT NULL COMMENT '申报编号',
    report_type VARCHAR(30) NOT NULL COMMENT '申报类型: INWARD=涉外收入 OUTWARD=涉外支出 DOMESTIC=境内收入',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(200) NOT NULL COMMENT '客户名称',
    transaction_no VARCHAR(64) NOT NULL COMMENT '业务编号',
    transaction_type VARCHAR(30) NOT NULL COMMENT '交易类型',
    transaction_amount DECIMAL(22,6) NOT NULL COMMENT '交易金额',
    transaction_currency VARCHAR(10) NOT NULL COMMENT '交易币种',
    cny_amount DECIMAL(22,6) NOT NULL COMMENT '人民币金额',
    exchange_rate DECIMAL(16,8) NOT NULL COMMENT '折算汇率',
    transaction_date DATE NOT NULL COMMENT '交易日期',
    settlement_date DATE NOT NULL COMMENT '交割日期',
    bop_code VARCHAR(20) NOT NULL COMMENT '国际收支交易编码',
    bop_name VARCHAR(200) NOT NULL COMMENT '交易编码名称',
    purpose_code VARCHAR(20) DEFAULT NULL COMMENT '交易附言编码',
    purpose_remark VARCHAR(500) DEFAULT NULL COMMENT '交易附言',
    counterparty_country VARCHAR(10) DEFAULT NULL COMMENT '对手方国家',
    counterparty_name VARCHAR(200) DEFAULT NULL COMMENT '对手方名称',
    report_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT=草稿 READY=待申报 SUBMITTED=已申报 ACCEPTED=已接收 REJECTED=已退回 CORRECTED=已修改',
    submit_time DATETIME DEFAULT NULL COMMENT '申报时间',
    regulatory_ref VARCHAR(64) DEFAULT NULL COMMENT '外汇局回执号',
    error_msg VARCHAR(500) DEFAULT NULL COMMENT '回退原因',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_report_no (report_no),
    KEY idx_customer_id (customer_id),
    KEY idx_transaction_date (transaction_date),
    KEY idx_report_status (report_status),
    KEY idx_biz_no (transaction_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国际收支申报表';

DROP TABLE IF EXISTS t_forex_settlement_report;
CREATE TABLE t_forex_settlement_report (
    id BIGINT NOT NULL COMMENT '主键ID',
    report_no VARCHAR(64) NOT NULL COMMENT '申报编号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    exchange_order_no VARCHAR(64) NOT NULL COMMENT '结售汇订单号',
    exchange_type VARCHAR(30) NOT NULL COMMENT '结售汇类型: SPOT/ FORWARD',
    deal_type VARCHAR(20) NOT NULL COMMENT '结汇/售汇',
    transaction_amount DECIMAL(22,6) NOT NULL COMMENT '交易金额(外币)',
    transaction_currency VARCHAR(10) NOT NULL COMMENT '交易币种',
    cny_amount DECIMAL(22,6) NOT NULL COMMENT '人民币金额',
    exchange_rate DECIMAL(16,8) NOT NULL COMMENT '成交汇率',
    transaction_date DATE NOT NULL COMMENT '交易日期',
    settle_date DATE NOT NULL COMMENT '交割日期',
    settlement_code VARCHAR(20) NOT NULL COMMENT '结售汇统计代码',
    report_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    submit_time DATETIME DEFAULT NULL,
    regulatory_ref VARCHAR(64) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_report_no (report_no),
    KEY idx_exchange_order (exchange_order_no),
    KEY idx_report_status (report_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结售汇申报表';

DROP TABLE IF EXISTS t_capital_account_report;
CREATE TABLE t_capital_account_report (
    id BIGINT NOT NULL COMMENT '主键ID',
    report_no VARCHAR(64) NOT NULL COMMENT '申报编号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    account_no VARCHAR(32) NOT NULL COMMENT '账号',
    report_type VARCHAR(30) NOT NULL COMMENT '申报类型',
    transaction_type VARCHAR(30) NOT NULL COMMENT '交易类型',
    transaction_amount DECIMAL(22,6) NOT NULL COMMENT '交易金额',
    transaction_currency VARCHAR(10) NOT NULL COMMENT '币种',
    transaction_date DATE NOT NULL COMMENT '交易日期',
    capital_code VARCHAR(20) NOT NULL COMMENT '资本项目编码',
    report_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    submit_time DATETIME DEFAULT NULL,
    regulatory_ref VARCHAR(64) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_report_no (report_no),
    KEY idx_customer_id (customer_id),
    KEY idx_report_status (report_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资本项目与外汇账户申报表';

DROP TABLE IF EXISTS t_regulatory_submit_log;
CREATE TABLE t_regulatory_submit_log (
    id BIGINT NOT NULL COMMENT '主键ID',
    report_type VARCHAR(30) NOT NULL COMMENT '申报类型',
    batch_no VARCHAR(64) NOT NULL COMMENT '批次号',
    total_count INT NOT NULL DEFAULT 0 COMMENT '总笔数',
    success_count INT NOT NULL DEFAULT 0 COMMENT '成功笔数',
    failed_count INT NOT NULL DEFAULT 0 COMMENT '失败笔数',
    submit_time DATETIME NOT NULL COMMENT '提交时间',
    response_time DATETIME DEFAULT NULL COMMENT '响应时间',
    response_code VARCHAR(20) DEFAULT NULL COMMENT '响应码',
    response_message VARCHAR(500) DEFAULT NULL COMMENT '响应信息',
    file_path VARCHAR(500) DEFAULT NULL COMMENT '报送文件路径',
    submit_status VARCHAR(20) NOT NULL COMMENT '状态: SUBMITTING/SUCCESS/FAILED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_batch_no (batch_no),
    KEY idx_submit_time (submit_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监管报送日志表';
