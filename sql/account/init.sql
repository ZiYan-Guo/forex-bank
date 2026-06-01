-- ============================================
-- 账户管理模块 - 数据库初始化脚本
-- ============================================
USE forex_account;

DROP TABLE IF EXISTS t_forex_account;
CREATE TABLE t_forex_account (
    id BIGINT NOT NULL COMMENT '主键ID',
    account_no VARCHAR(32) NOT NULL COMMENT '账号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    account_type VARCHAR(20) NOT NULL COMMENT '账户类型: CURRENT=经常项目 CAPITAL=资本项目 SETTLEMENT=结算户 SAVING=储蓄户',
    currency VARCHAR(10) NOT NULL COMMENT '币种',
    account_name VARCHAR(200) NOT NULL COMMENT '账户名称',
    balance DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '余额',
    frozen_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '冻结金额',
    available_balance DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '可用余额',
    open_date DATE NOT NULL COMMENT '开户日期',
    open_branch VARCHAR(50) DEFAULT NULL COMMENT '开户机构',
    account_status VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '账户状态: NORMAL=正常 FROZEN=冻结 CLOSED=已销户',
    interest_rate DECIMAL(10,6) DEFAULT NULL COMMENT '利率',
    is_interest_bearing TINYINT NOT NULL DEFAULT 0 COMMENT '是否计息',
    central_bank_report_status TINYINT NOT NULL DEFAULT 0 COMMENT '央行报送状态',
    last_report_time DATETIME DEFAULT NULL COMMENT '最后报送时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_account_no (account_no),
    KEY idx_customer_id (customer_id),
    KEY idx_account_status (account_status),
    KEY idx_customer_currency (customer_id, currency)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外汇账户表';

DROP TABLE IF EXISTS t_account_transaction;
CREATE TABLE t_account_transaction (
    id BIGINT NOT NULL COMMENT '主键ID',
    transaction_no VARCHAR(64) NOT NULL COMMENT '交易流水号',
    account_id BIGINT NOT NULL COMMENT '账户ID',
    account_no VARCHAR(32) NOT NULL COMMENT '账号',
    transaction_type VARCHAR(30) NOT NULL COMMENT '交易类型: DEPOSIT=存入 WITHDRAW=支取 TRANSFER_IN=转入 TRANSFER_OUT=转出 INTEREST=结息 FEE=手续费',
    amount DECIMAL(22,6) NOT NULL COMMENT '交易金额',
    currency VARCHAR(10) NOT NULL COMMENT '币种',
    balance_before DECIMAL(22,6) NOT NULL COMMENT '交易前余额',
    balance_after DECIMAL(22,6) NOT NULL COMMENT '交易后余额',
    related_biz_no VARCHAR(64) DEFAULT NULL COMMENT '关联业务编号',
    related_biz_type VARCHAR(30) DEFAULT NULL COMMENT '关联业务类型',
    transaction_time DATETIME NOT NULL COMMENT '交易时间',
    summary VARCHAR(500) DEFAULT NULL COMMENT '摘要',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_transaction_no (transaction_no),
    KEY idx_account_id_time (account_id, transaction_time DESC),
    KEY idx_account_no_time (account_no, transaction_time DESC),
    KEY idx_transaction_time (transaction_time),
    KEY idx_related_biz (related_biz_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户交易流水表';

DROP TABLE IF EXISTS t_account_balance_snapshot;
CREATE TABLE t_account_balance_snapshot (
    id BIGINT NOT NULL COMMENT '主键ID',
    account_id BIGINT NOT NULL COMMENT '账户ID',
    account_no VARCHAR(32) NOT NULL COMMENT '账号',
    balance DECIMAL(22,6) NOT NULL COMMENT '余额',
    snapshot_date DATE NOT NULL COMMENT '快照日期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_account_date (account_id, snapshot_date),
    KEY idx_snapshot_date (snapshot_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户余额快照表(日终)';
