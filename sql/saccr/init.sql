-- ============================================
-- SA-CCR计量模块 - 数据库初始化脚本
-- ============================================
USE forex_saccr;

DROP TABLE IF EXISTS t_saccr_result;
CREATE TABLE t_saccr_result (
    id BIGINT NOT NULL COMMENT '主键ID',
    calc_no VARCHAR(64) NOT NULL COMMENT '计算编号',
    trade_id BIGINT NOT NULL COMMENT '交易ID',
    trade_no VARCHAR(64) NOT NULL COMMENT '交易编号',
    counter_party_id VARCHAR(64) NOT NULL COMMENT '对手方ID',
    calc_date DATE NOT NULL COMMENT '计算日期',
    rc DECIMAL(22,6) DEFAULT 0 COMMENT '重置成本(RC)',
    pfe DECIMAL(22,6) DEFAULT 0 COMMENT '潜在未来敞口(PFE)',
    exposure DECIMAL(22,6) DEFAULT 0 COMMENT '风险敞口(Exposure)',
    alpha DECIMAL(10,4) DEFAULT 1.4000 COMMENT '监管乘子因子(Alpha)',
    calc_method VARCHAR(30) NOT NULL DEFAULT 'SA-CCR' COMMENT '计算方法',
    result_json TEXT COMMENT '计算结果JSON',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_calc_no (calc_no),
    KEY idx_trade_id (trade_id),
    KEY idx_calc_date (calc_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SA-CCR计算结果表';

DROP TABLE IF EXISTS t_simm_result;
CREATE TABLE t_simm_result (
    id BIGINT NOT NULL COMMENT '主键ID',
    calc_no VARCHAR(64) NOT NULL COMMENT '计算编号',
    trade_id BIGINT NOT NULL COMMENT '交易ID',
    trade_no VARCHAR(64) NOT NULL COMMENT '交易编号',
    calc_date DATE NOT NULL COMMENT '计算日期',
    notional_amount DECIMAL(22,6) DEFAULT NULL COMMENT '名义本金',
    delta_margin DECIMAL(22,6) DEFAULT 0 COMMENT 'Delta保证金',
    vega_margin DECIMAL(22,6) DEFAULT 0 COMMENT 'Vega保证金',
    curvature_margin DECIMAL(22,6) DEFAULT 0 COMMENT '曲率保证金',
    total_margin DECIMAL(22,6) DEFAULT 0 COMMENT '总保证金',
    calc_method VARCHAR(30) NOT NULL DEFAULT 'ISDA-SIMM' COMMENT '计算方法',
    sensitivities_json TEXT COMMENT '敏感度数据JSON',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_calc_no (calc_no),
    KEY idx_trade_id (trade_id),
    KEY idx_calc_date (calc_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ISDA SIMM计算结果表';
