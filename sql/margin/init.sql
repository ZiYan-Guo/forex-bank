-- ============================================
-- 保证金管理模块 - 数据库初始化脚本
-- ============================================
USE forex_margin;

DROP TABLE IF EXISTS t_margin_account;
CREATE TABLE t_margin_account (
    id BIGINT NOT NULL COMMENT '主键ID',
    margin_no VARCHAR(64) NOT NULL COMMENT '保证金编号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    trade_id BIGINT DEFAULT NULL COMMENT '关联交易ID',
    margin_type VARCHAR(30) NOT NULL COMMENT '保证金类型: INITIAL=初始 VARIATION=变动 ADDITIONAL=追加',
    margin_currency VARCHAR(10) NOT NULL COMMENT '保证金币种',
    required_amount DECIMAL(22,6) NOT NULL COMMENT '要求金额',
    deposited_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '已缴金额',
    shortfall_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '差额(=要求-已缴)',
    margin_rate DECIMAL(10,6) DEFAULT NULL COMMENT '保证金率%',
    call_date DATETIME DEFAULT NULL COMMENT '追缴日期',
    due_date DATETIME DEFAULT NULL COMMENT '应缴日期',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING=待缴 PAID=已缴 PARTIAL=部分缴 CALLED=已追缴 RELEASED=已释放 CANCELLED=已取消',
    collateral_type VARCHAR(30) DEFAULT 'CASH' COMMENT '担保品类型: CASH=现金 BOND=债券',
    release_reason VARCHAR(200) DEFAULT NULL COMMENT '释放原因',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_margin_no (margin_no),
    KEY idx_customer_id (customer_id),
    KEY idx_trade_id (trade_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保证金账户表';

DROP TABLE IF EXISTS t_margin_call;
CREATE TABLE t_margin_call (
    id BIGINT NOT NULL COMMENT '主键ID',
    margin_id BIGINT NOT NULL COMMENT '保证金ID',
    margin_no VARCHAR(64) NOT NULL COMMENT '保证金编号',
    call_type VARCHAR(20) NOT NULL COMMENT '追缴类型: MARGIN_CALL=追缴 MARGIN_RELEASE=释放',
    call_amount DECIMAL(22,6) NOT NULL COMMENT '追缴/释放金额',
    call_date DATETIME NOT NULL COMMENT '通知日期',
    response_date DATETIME DEFAULT NULL COMMENT '响应日期',
    response_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '响应状态: PENDING=待处理 AGREED=已同意 DISPUTED=争议',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_margin_id (margin_id),
    KEY idx_call_date (call_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保证金追缴记录表';
