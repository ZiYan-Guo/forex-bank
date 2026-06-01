-- ============================================
-- 客户管理模块 - 数据库初始化脚本
-- ============================================
USE forex_customer;

DROP TABLE IF EXISTS t_customer;
CREATE TABLE t_customer (
    id BIGINT NOT NULL COMMENT '主键ID',
    customer_no VARCHAR(32) NOT NULL COMMENT '客户编号',
    customer_type TINYINT NOT NULL COMMENT '客户类型: 1=对公 2=对私 3=同业',
    customer_name VARCHAR(200) NOT NULL COMMENT '客户名称',
    english_name VARCHAR(200) DEFAULT NULL COMMENT '英文名称',
    cert_type VARCHAR(20) DEFAULT NULL COMMENT '证件类型',
    cert_no VARCHAR(100) DEFAULT NULL COMMENT '证件号码',
    country_code VARCHAR(10) DEFAULT NULL COMMENT '国家代码',
    address VARCHAR(500) DEFAULT NULL COMMENT '地址',
    contact_person VARCHAR(100) DEFAULT NULL COMMENT '联系人',
    contact_phone VARCHAR(30) DEFAULT NULL COMMENT '联系电话',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    risk_level TINYINT NOT NULL DEFAULT 1 COMMENT '风险等级: 1=低 2=中 3=高 9=禁止',
    risk_reason VARCHAR(500) DEFAULT NULL COMMENT '风险评级原因',
    due_diligence_status TINYINT NOT NULL DEFAULT 0 COMMENT '尽调状态: 0=未尽调 1=尽调中 2=已完成 3=已过期',
    due_diligence_date DATETIME DEFAULT NULL COMMENT '尽调完成日期',
    cross_border_platform_id VARCHAR(64) DEFAULT NULL COMMENT '跨境金融服务平台接入ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1=正常 0=禁用',
    remark VARCHAR(1000) DEFAULT NULL COMMENT '备注',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号(乐观锁)',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_customer_no (customer_no),
    UNIQUE KEY uk_cert_no (cert_type, cert_no),
    KEY idx_customer_name (customer_name),
    KEY idx_risk_level (risk_level),
    KEY idx_customer_type (customer_type),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信息表';

DROP TABLE IF EXISTS t_customer_credit_limit;
CREATE TABLE t_customer_credit_limit (
    id BIGINT NOT NULL COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    limit_type VARCHAR(30) NOT NULL COMMENT '额度类型: FOREX=结售汇 PAYMENT=汇款 SETTLEMENT=结算 TRADE=外汇交易',
    currency VARCHAR(10) NOT NULL DEFAULT 'CNY' COMMENT '币种',
    total_limit DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '总额度',
    used_limit DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '已用额度',
    available_limit DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '可用额度',
    effective_date DATE NOT NULL COMMENT '生效日期',
    expire_date DATE NOT NULL COMMENT '到期日期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1=生效 0=失效',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_customer_id (customer_id),
    KEY idx_customer_limit_type (customer_id, limit_type, currency),
    KEY idx_expire_date (expire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户额度表';

DROP TABLE IF EXISTS t_customer_quota;
CREATE TABLE t_customer_quota (
    id BIGINT NOT NULL COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    quota_year VARCHAR(4) NOT NULL COMMENT '年度',
    quota_type VARCHAR(30) NOT NULL COMMENT '额度类型',
    quota_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '额度金额',
    used_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '已用金额',
    currency VARCHAR(10) NOT NULL DEFAULT 'USD' COMMENT '币种',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_customer_quota (customer_id, quota_year, quota_type, currency),
    KEY idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户外汇额度表';
