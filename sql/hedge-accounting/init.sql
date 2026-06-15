CREATE DATABASE IF NOT EXISTS forex_hedge_accounting DEFAULT CHARACTER SET utf8mb4;

USE forex_hedge_accounting;

CREATE TABLE t_hedge_relationship (
    id BIGINT,
    relation_id VARCHAR(64) NOT NULL,
    customer_id BIGINT,
    hedge_type VARCHAR(30),
    hedged_item TEXT,
    hedging_instrument TEXT,
    hedged_amount DECIMAL(22,6),
    hedged_currency VARCHAR(10),
    instrument_notional DECIMAL(22,6),
    designation_date DATE,
    de_designation_date DATE,
    relationship_status VARCHAR(20),
    effectiveness_ratio DECIMAL(6,4),
    ifrs_standard VARCHAR(20),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME,
    version INT DEFAULT 0,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY(id),
    UNIQUE KEY uk_relation_id(relation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套期关系';

CREATE TABLE t_hedge_effectiveness_test (
    id BIGINT,
    relation_id VARCHAR(64),
    test_date DATE,
    test_type VARCHAR(20),
    test_method VARCHAR(30),
    test_result DECIMAL(6,4),
    result_status VARCHAR(10),
    remarks TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(id),
    KEY idx_relation_id(relation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='有效性测试';
