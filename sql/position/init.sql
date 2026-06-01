-- ============================================
-- 敞口管理模块 - 数据库初始化脚本
-- ============================================
USE forex_position;

DROP TABLE IF EXISTS t_position;
CREATE TABLE t_position (
    id BIGINT NOT NULL COMMENT '主键ID',
    position_no VARCHAR(64) NOT NULL COMMENT '敞口编号',
    currency_pair VARCHAR(20) NOT NULL COMMENT '货币对',
    position_type VARCHAR(30) NOT NULL COMMENT '敞口类型: SPOT=即期 FORWARD=远期 SWAP=掉期 OPTION=期权 AGGREGATE=汇总',
    position_currency VARCHAR(10) NOT NULL COMMENT '敞口币种',
    long_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '多头金额',
    short_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '空头金额',
    net_position DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '净头寸(多-空)',
    position_limit DECIMAL(22,6) DEFAULT NULL COMMENT '敞口限额',
    limit_usage_pct DECIMAL(8,4) DEFAULT 0 COMMENT '限额使用率%',
    position_date DATE NOT NULL COMMENT '敞口日期',
    trader_id BIGINT DEFAULT NULL COMMENT '交易员ID',
    branch_code VARCHAR(20) DEFAULT NULL COMMENT '机构号',
    risk_level VARCHAR(10) DEFAULT 'NORMAL' COMMENT '风险等级: NORMAL=正常 WARNING=预警 BREACH=超限',
    hedging_action VARCHAR(50) DEFAULT NULL COMMENT '对冲建议',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_position (currency_pair, position_type, position_date, trader_id),
    KEY idx_position_date (position_date),
    KEY idx_trader_id (trader_id),
    KEY idx_risk_level (risk_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外汇敞口表';

DROP TABLE IF EXISTS t_position_limit_config;
CREATE TABLE t_position_limit_config (
    id BIGINT NOT NULL COMMENT '主键ID',
    currency VARCHAR(10) NOT NULL COMMENT '币种',
    limit_type VARCHAR(30) NOT NULL COMMENT '限额类型: OVERALL=总体 TRADER=交易员 INTRADAY=日内 OVERNIGHT=隔夜',
    limit_amount DECIMAL(22,6) NOT NULL COMMENT '限额金额',
    warning_pct DECIMAL(5,2) DEFAULT 80 COMMENT '预警比例%',
    is_enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_limit (currency, limit_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敞口限额配置表';

-- Seed position limit config data
INSERT INTO t_position_limit_config (id, currency, limit_type, limit_amount, warning_pct, is_enabled) VALUES
(1, 'USD', 'OVERALL', 10000000.00, 80.00, 1),
(2, 'EUR', 'OVERALL', 8000000.00, 80.00, 1),
(3, 'JPY', 'OVERALL', 1000000000.00, 80.00, 1),
(4, 'USD', 'INTRADAY', 5000000.00, 80.00, 1),
(5, 'USD', 'OVERNIGHT', 2000000.00, 80.00, 1);
