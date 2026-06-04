CREATE DATABASE IF NOT EXISTS forex_ai DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE forex_ai;

CREATE TABLE IF NOT EXISTS t_rate_prediction (
    id BIGINT NOT NULL, pred_no VARCHAR(64) NOT NULL,
    currency_pair VARCHAR(20) NOT NULL, pred_type VARCHAR(20) NOT NULL COMMENT 'HOURLY/DAILY/WEEKLY',
    pred_time DATETIME NOT NULL, target_time DATETIME NOT NULL,
    predicted_rate DECIMAL(16,8) NOT NULL, lower_bound DECIMAL(16,8), upper_bound DECIMAL(16,8),
    confidence DECIMAL(5,4), model_name VARCHAR(50),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_time DATETIME, version INT DEFAULT 0, deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id), UNIQUE KEY uk_pred_no (pred_no), KEY idx_ccy_pair_time (currency_pair, target_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI汇率预测表';

CREATE TABLE IF NOT EXISTS t_chat_session (
    id BIGINT NOT NULL, session_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64), user_name VARCHAR(100), session_type VARCHAR(20) COMMENT 'SUPPORT/TRADING/AUDIT',
    title VARCHAR(200), status VARCHAR(20) DEFAULT 'ACTIVE',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_time DATETIME, version INT DEFAULT 0, deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id), UNIQUE KEY uk_session_id (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话会话表';

CREATE TABLE IF NOT EXISTS t_chat_message (
    id BIGINT NOT NULL, session_id VARCHAR(64) NOT NULL, role VARCHAR(20) NOT NULL COMMENT 'user/assistant/system',
    content TEXT NOT NULL, sources TEXT COMMENT 'RAG检索来源', confidence DECIMAL(5,4),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id), KEY idx_session_id (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话消息表';

CREATE TABLE IF NOT EXISTS t_risk_ai_assessment (
    id BIGINT NOT NULL, assessment_id VARCHAR(64) NOT NULL,
    customer_id BIGINT, biz_no VARCHAR(64), risk_type VARCHAR(30) COMMENT 'AML/FRAUD/SANCTION',
    risk_score DECIMAL(5,2), risk_level VARCHAR(20),
    ai_analysis TEXT, recommendation TEXT, data_points_json TEXT,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_time DATETIME, version INT DEFAULT 0, deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id), UNIQUE KEY uk_assessment_id (assessment_id), KEY idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI风险评估表';

CREATE TABLE IF NOT EXISTS t_document_audit (
    id BIGINT NOT NULL, audit_id VARCHAR(64) NOT NULL,
    biz_no VARCHAR(64), doc_type VARCHAR(30) COMMENT 'INVOICE/BL/CUSTOMS',
    ocr_result TEXT, comparison_result TEXT, is_consistent TINYINT,
    discrepancy_detail TEXT, audit_opinion TEXT, confidence_score DECIMAL(5,4),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_time DATETIME, version INT DEFAULT 0, deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id), UNIQUE KEY uk_audit_id (audit_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI审单结果表';
