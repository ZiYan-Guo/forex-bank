-- ============================================
-- 簿记核算模块 - 数据库初始化脚本
-- ============================================
USE forex_bookkeeping;

DROP TABLE IF EXISTS t_journal_entry;
CREATE TABLE t_journal_entry (
    id BIGINT NOT NULL COMMENT '主键ID',
    voucher_no VARCHAR(64) NOT NULL COMMENT '凭证号',
    voucher_date DATE NOT NULL COMMENT '凭证日期',
    fiscal_period VARCHAR(6) NOT NULL COMMENT '会计期间(yyyyMM)',
    biz_type VARCHAR(30) NOT NULL COMMENT '业务类型: EXCHANGE=结售汇 TRADING=外汇交易 PAYMENT=跨境支付 SETTLEMENT=国际结算 FEE=手续费',
    biz_no VARCHAR(64) DEFAULT NULL COMMENT '业务编号',
    currency VARCHAR(10) NOT NULL COMMENT '币种',
    amount DECIMAL(22,6) NOT NULL COMMENT '金额',
    entry_direction VARCHAR(10) NOT NULL COMMENT '方向: DEBIT=借 CREDIT=贷',
    account_code VARCHAR(30) NOT NULL COMMENT '科目代码',
    account_name VARCHAR(100) NOT NULL COMMENT '科目名称',
    opposite_account_code VARCHAR(30) DEFAULT NULL COMMENT '对方科目代码',
    summary VARCHAR(500) NOT NULL COMMENT '摘要',
    entry_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING=待记账 POSTED=已记账 REVERSED=已冲正',
    reversed_voucher_no VARCHAR(64) DEFAULT NULL COMMENT '冲正凭证号',
    posted_time DATETIME DEFAULT NULL COMMENT '记账时间',
    operator_id BIGINT DEFAULT NULL COMMENT '操作员ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_voucher_no (voucher_no),
    KEY idx_voucher_date (voucher_date),
    KEY idx_fiscal_period (fiscal_period),
    KEY idx_biz_no (biz_no),
    KEY idx_account_code (account_code),
    KEY idx_entry_status (entry_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会计分录表';

DROP TABLE IF EXISTS t_account_subject;
CREATE TABLE t_account_subject (
    id BIGINT NOT NULL COMMENT '主键ID',
    subject_code VARCHAR(30) NOT NULL COMMENT '科目代码',
    subject_name VARCHAR(100) NOT NULL COMMENT '科目名称',
    subject_type VARCHAR(20) NOT NULL COMMENT '科目类型: ASSET=资产 LIABILITY=负债 EQUITY=权益 INCOME=收入 EXPENSE=费用',
    parent_code VARCHAR(30) DEFAULT NULL COMMENT '上级科目代码',
    subject_level TINYINT NOT NULL DEFAULT 1 COMMENT '科目级别',
    is_leaf TINYINT NOT NULL DEFAULT 1 COMMENT '是否叶子节点',
    currency VARCHAR(10) DEFAULT NULL COMMENT '核算币种',
    is_enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_subject_code (subject_code),
    KEY idx_parent_code (parent_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会计科目表';

DROP TABLE IF EXISTS t_ledger;
CREATE TABLE t_ledger (
    id BIGINT NOT NULL COMMENT '主键ID',
    account_code VARCHAR(30) NOT NULL COMMENT '科目代码',
    account_name VARCHAR(100) NOT NULL COMMENT '科目名称',
    currency VARCHAR(10) NOT NULL COMMENT '币种',
    fiscal_period VARCHAR(6) NOT NULL COMMENT '会计期间',
    opening_balance DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '期初余额',
    debit_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '本期借方发生额',
    credit_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '本期贷方发生额',
    closing_balance DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '期末余额',
    balance_direction VARCHAR(10) NOT NULL COMMENT '余额方向: DEBIT=借 CREDIT=贷',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_account_period_ccy (account_code, currency, fiscal_period)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分户账表';
