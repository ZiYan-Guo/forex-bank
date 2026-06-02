-- ============================================
-- 衍生品估值模块 - 数据库初始化脚本
-- ============================================
USE forex_valuation;

DROP TABLE IF EXISTS t_valuation_result;
CREATE TABLE t_valuation_result (
    id BIGINT NOT NULL COMMENT '主键ID',
    trade_id BIGINT NOT NULL COMMENT '交易ID',
    trade_no VARCHAR(64) NOT NULL COMMENT '交易编号',
    trade_type VARCHAR(30) NOT NULL COMMENT '交易类型',
    valuation_date DATE NOT NULL COMMENT '估值日期',
    currency_pair VARCHAR(20) NOT NULL COMMENT '货币对',
    notional_amount DECIMAL(22,6) NOT NULL COMMENT '名义本金',
    fair_value DECIMAL(22,6) NOT NULL COMMENT '公允价值',
    pnl DECIMAL(22,6) DEFAULT 0 COMMENT '当日损益',
    cumulative_pnl DECIMAL(22,6) DEFAULT 0 COMMENT '累计损益',
    valuation_method VARCHAR(30) NOT NULL COMMENT '估值方法: BS=Black-Scholes GK=Garman-Kohlhagen DCF=现金流折现 MONTE_CARLO=蒙特卡洛',
    model_params JSON DEFAULT NULL COMMENT '模型参数(JSON)',
    market_data_snapshot JSON DEFAULT NULL COMMENT '市场数据快照',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_trade_date (trade_id, valuation_date),
    KEY idx_valuation_date (valuation_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='估值结果表';

DROP TABLE IF EXISTS t_valuation_model_config;
CREATE TABLE t_valuation_model_config (
    id BIGINT NOT NULL COMMENT '主键ID',
    model_name VARCHAR(50) NOT NULL COMMENT '模型名称',
    model_type VARCHAR(30) NOT NULL COMMENT '模型类型',
    product_type VARCHAR(30) NOT NULL COMMENT '适用产品类型',
    model_parameters JSON NOT NULL COMMENT '模型参数配置',
    is_enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_model_product (model_type, product_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='估值模型配置表';

DROP TABLE IF EXISTS t_pnl_attribution;
CREATE TABLE t_pnl_attribution (
    id BIGINT NOT NULL COMMENT '主键',
    attrib_no VARCHAR(64) NOT NULL COMMENT '归因编号',
    trade_id BIGINT NOT NULL COMMENT '交易ID',
    trade_no VARCHAR(64) NOT NULL COMMENT '交易编号',
    attrib_date DATE NOT NULL COMMENT '归因日期',
    total_pnl DECIMAL(22,6) DEFAULT 0 COMMENT '总损益',
    delta_pnl DECIMAL(22,6) DEFAULT 0 COMMENT 'Delta损益',
    theta_pnl DECIMAL(22,6) DEFAULT 0 COMMENT 'Theta损益',
    gamma_pnl DECIMAL(22,6) DEFAULT 0 COMMENT 'Gamma损益',
    vega_pnl DECIMAL(22,6) DEFAULT 0 COMMENT 'Vega损益',
    carry_pnl DECIMAL(22,6) DEFAULT 0 COMMENT '息差损益',
    trade_pnl DECIMAL(22,6) DEFAULT 0 COMMENT '交易操作损益',
    tariff_type VARCHAR(30) COMMENT '归因维度类型',
    tariff_value VARCHAR(100) COMMENT '归因维度值',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_trade_attrib_date (trade_id, attrib_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='损益归因表';
