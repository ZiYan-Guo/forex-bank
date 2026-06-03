-- ============================================
-- 清算模块 - 数据库初始化脚本
-- ============================================
USE forex_clearing;

DROP TABLE IF EXISTS t_clearing_instruction;
CREATE TABLE t_clearing_instruction (
    id BIGINT NOT NULL COMMENT '主键ID',
    instruction_no VARCHAR(64) NOT NULL COMMENT '清算指令编号',
    biz_type VARCHAR(30) NOT NULL COMMENT '业务类型',
    biz_no VARCHAR(64) NOT NULL COMMENT '业务编号',
    clearing_channel VARCHAR(30) NOT NULL COMMENT '清算渠道: SWIFT/CIPS/CFXPS/LOCAL',
    nostro_account VARCHAR(30) NOT NULL COMMENT '往来账户',
    counter_party_account VARCHAR(50) DEFAULT NULL COMMENT '对手账户',
    pay_currency VARCHAR(10) NOT NULL COMMENT '支付币种',
    pay_amount DECIMAL(22,6) NOT NULL COMMENT '支付金额',
    receive_currency VARCHAR(10) NOT NULL COMMENT '收款币种',
    receive_amount DECIMAL(22,6) NOT NULL COMMENT '收款金额',
    value_date DATE NOT NULL COMMENT '起息日',
    settlement_date DATE NOT NULL COMMENT '交割日',
    settlement_type VARCHAR(20) DEFAULT 'GROSS' COMMENT '交收方式: GROSS=全额 NET=净额',
    instruction_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT=草稿 GENERATED=已生成 SENT=已发送 ACK=已确认 SETTLED=已结算 FAILED=失败 CANCELLED=已取消',
    swift_ref VARCHAR(64) DEFAULT NULL COMMENT 'SWIFT参考号',
    cips_ref VARCHAR(64) DEFAULT NULL COMMENT 'CIPS参考号',
    nostro_balance_before DECIMAL(22,6) DEFAULT NULL COMMENT '清算前余额',
    nostro_balance_after DECIMAL(22,6) DEFAULT NULL COMMENT '清算后余额',
    send_time DATETIME DEFAULT NULL COMMENT '发送时间',
    ack_time DATETIME DEFAULT NULL COMMENT '确认时间',
    settle_time DATETIME DEFAULT NULL COMMENT '交收时间',
    operator_id BIGINT DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_instruction_no (instruction_no),
    KEY idx_biz_no (biz_no),
    KEY idx_value_date (value_date),
    KEY idx_instruction_status (instruction_status),
    KEY idx_channel (clearing_channel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='清算指令表';

DROP TABLE IF EXISTS t_settlement_batch;
CREATE TABLE t_settlement_batch (
    id BIGINT NOT NULL COMMENT '主键ID',
    batch_no VARCHAR(64) NOT NULL COMMENT '批次号',
    batch_date DATE NOT NULL COMMENT '批次日期',
    clearing_channel VARCHAR(30) NOT NULL COMMENT '清算渠道',
    total_count INT NOT NULL DEFAULT 0 COMMENT '总笔数',
    total_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '总金额',
    net_amount DECIMAL(22,6) DEFAULT NULL COMMENT '净额',
    batch_status VARCHAR(20) NOT NULL DEFAULT 'OPEN' COMMENT '状态: OPEN=开放 CLOSED=已关闭 SETTLED=已结算',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_batch_no (batch_no),
    KEY idx_batch_date_channel (batch_date, clearing_channel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='清算批次表';

DROP TABLE IF EXISTS t_confirmation_match;
CREATE TABLE IF NOT EXISTS t_confirmation_match (
    id BIGINT NOT NULL,
    confirm_id VARCHAR(64) NOT NULL,
    trade_no VARCHAR(64) NOT NULL,
    trade_type VARCHAR(30),
    confirm_flag VARCHAR(20) NOT NULL COMMENT 'CENTRALIZED/BILATERAL',
    currency_pair VARCHAR(20),
    direction VARCHAR(10),
    amount DECIMAL(22,6),
    rate DECIMAL(16,8),
    value_date DATE,
    counterparty VARCHAR(200),
    match_status VARCHAR(30) DEFAULT 'UNMATCHED',
    external_ref VARCHAR(64),
    discrepancy_detail VARCHAR(500),
    retry_count INT DEFAULT 0,
    next_retry_at DATETIME,
    resolution_action VARCHAR(30),
    resolution_comment VARCHAR(500),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_confirm_id (confirm_id),
    KEY idx_trade_no (trade_no),
    KEY idx_match_status (match_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易确认匹配表(JR/T 0310-2025)';

DROP TABLE IF EXISTS t_settlement_tracker;
CREATE TABLE IF NOT EXISTS t_settlement_tracker (
    id BIGINT NOT NULL,
    tracking_id VARCHAR(64) NOT NULL,
    payment_no VARCHAR(64),
    instruction_no VARCHAR(64),
    current_status VARCHAR(30) NOT NULL,
    status_changed_at DATETIME,
    channel VARCHAR(30),
    gpi_status VARCHAR(100),
    exception_reason VARCHAR(200),
    exception_detail VARCHAR(500),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tracking_id (tracking_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结算追踪表';

CREATE TABLE IF NOT EXISTS t_pvp_settlement_pair (
    id BIGINT NOT NULL,
    pair_id VARCHAR(64) NOT NULL,
    pay_instruction_id BIGINT NOT NULL,
    pay_instruction_no VARCHAR(64) NOT NULL,
    receive_instruction_id BIGINT NOT NULL,
    receive_instruction_no VARCHAR(64) NOT NULL,
    pay_currency VARCHAR(10) NOT NULL,
    pay_amount DECIMAL(22,6) NOT NULL,
    receive_currency VARCHAR(10) NOT NULL,
    receive_amount DECIMAL(22,6) NOT NULL,
    settlement_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'PAIRED',
    failure_reason VARCHAR(500),
    settled_at DATETIME,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME,
    version INT DEFAULT 0,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pair_id (pair_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PVP交收对表';

CREATE TABLE IF NOT EXISTS t_cls_session (
    id BIGINT NOT NULL,
    session_id VARCHAR(64) NOT NULL,
    settlement_date DATE NOT NULL,
    pay_in_window_start DATETIME,
    pay_in_window_end DATETIME,
    session_status VARCHAR(20) DEFAULT 'SCHEDULED',
    total_pay_in_sum DECIMAL(22,6) DEFAULT 0,
    total_pay_out_sum DECIMAL(22,6) DEFAULT 0,
    net_position DECIMAL(22,6) DEFAULT 0,
    position_json TEXT,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME,
    version INT DEFAULT 0,
    deleted TINYINT DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_session_id (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CLS结算场次表';
