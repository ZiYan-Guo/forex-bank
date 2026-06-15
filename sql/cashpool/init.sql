-- ============================================
-- 资金池管理模块 - 数据库初始化脚本
-- Cashpool Module - Database Initialization Script
-- ============================================

CREATE DATABASE IF NOT EXISTS forex_cashpool DEFAULT CHARACTER SET utf8mb4;

USE forex_cashpool;

-- ----------------------------
-- 资金池主账户表
-- Cash Pool Main Account Table
-- ----------------------------
DROP TABLE IF EXISTS t_cash_pool;
CREATE TABLE t_cash_pool (
    id BIGINT NOT NULL COMMENT '主键ID',
    pool_id VARCHAR(64) NOT NULL COMMENT '资金池编号',
    main_account_id BIGINT COMMENT '主账户ID',
    pool_name VARCHAR(200) NOT NULL DEFAULT '' COMMENT '资金池名称',
    pool_currency VARCHAR(10) NOT NULL COMMENT '资金池币种',
    total_limit DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '总额度',
    used_limit DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '已使用额度',
    available_limit DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '可用额度',
    pool_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '资金池状态: ACTIVE=活跃 SUSPENDED=暂停 CLOSED=关闭',
    effective_date DATE NOT NULL COMMENT '生效日期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pool_id (pool_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金池主账户';

-- ----------------------------
-- 资金池成员表
-- Cash Pool Members Table
-- ----------------------------
DROP TABLE IF EXISTS t_pool_member;
CREATE TABLE t_pool_member (
    id BIGINT NOT NULL COMMENT '主键ID',
    pool_id VARCHAR(64) NOT NULL COMMENT '资金池编号',
    member_account_id BIGINT NOT NULL COMMENT '成员账户ID',
    member_type VARCHAR(20) NOT NULL COMMENT '成员类型: DOMESTIC=境内 OVERSEAS=境外',
    currency VARCHAR(10) NOT NULL COMMENT '币种',
    settlement_mode VARCHAR(20) NOT NULL COMMENT '结算模式: REALTIME=实时 DAILY=每日 WEEKLY=每周',
    contribution_limit DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '贡献额度',
    join_date DATE NOT NULL COMMENT '加入日期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_pool_id (pool_id),
    KEY idx_member_account_id (member_account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金池成员';

-- ----------------------------
-- 境外放款合同表
-- Overseas Lending Contracts Table
-- ----------------------------
DROP TABLE IF EXISTS t_overseas_lending;
CREATE TABLE t_overseas_lending (
    id BIGINT NOT NULL COMMENT '主键ID',
    contract_no VARCHAR(64) NOT NULL COMMENT '合同编号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    loan_amount DECIMAL(22,6) NOT NULL COMMENT '放款金额',
    loan_currency VARCHAR(10) NOT NULL COMMENT '放款币种',
    interest_rate DECIMAL(8,6) NOT NULL COMMENT '利率',
    start_date DATE NOT NULL COMMENT '放款日期',
    end_date DATE NOT NULL COMMENT '到期日期',
    repayment_method VARCHAR(30) NOT NULL COMMENT '还款方式: BULLET=一次性还本付息 EQUAL_INSTALLMENT=等额本息',
    loan_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '放款状态: DRAFT=草稿 SUBMITTED=已提交 APPROVED=已审批 ACTIVE=生效中 REPAID=已结清 OVERDUE=逾期 CANCELLED=已取消',
    outstanding_principal DECIMAL(22,6) NOT NULL COMMENT '未偿还本金',
    total_interest DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '累计利息',
    pool_id BIGINT COMMENT '关联资金池ID(可选)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_contract_no (contract_no),
    KEY idx_customer_id (customer_id),
    KEY idx_pool_id (pool_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='境外放款合同';
