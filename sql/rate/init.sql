-- ============================================
-- 汇率管理模块 - 数据库初始化脚本
-- ============================================
USE forex_rate;

DROP TABLE IF EXISTS t_exchange_rate;
CREATE TABLE t_exchange_rate (
    id BIGINT NOT NULL COMMENT '主键ID',
    currency_pair VARCHAR(20) NOT NULL COMMENT '货币对: USD_CNY, EUR_USD',
    base_currency VARCHAR(10) NOT NULL COMMENT '基础货币',
    quote_currency VARCHAR(10) NOT NULL COMMENT '报价货币',
    bid_rate DECIMAL(16,8) NOT NULL COMMENT '买入价',
    ask_rate DECIMAL(16,8) NOT NULL COMMENT '卖出价',
    mid_rate DECIMAL(16,8) DEFAULT NULL COMMENT '中间价',
    spread DECIMAL(16,8) DEFAULT 0 COMMENT '点差',
    rate_source VARCHAR(30) NOT NULL COMMENT '汇率来源: CFETS, MANUAL, MARKET',
    rate_date DATE NOT NULL COMMENT '汇率日期',
    rate_time DATETIME NOT NULL COMMENT '汇率时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1=有效 0=失效',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_currency_pair_time (currency_pair, rate_time DESC),
    KEY idx_rate_date (rate_date),
    KEY idx_rate_source (rate_source),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='汇率牌价表';

DROP TABLE IF EXISTS t_rate_publish_config;
CREATE TABLE t_rate_publish_config (
    id BIGINT NOT NULL COMMENT '主键ID',
    channel_code VARCHAR(30) NOT NULL COMMENT '渠道编码: CORE, EBANK, MBANK, COUNTER',
    channel_name VARCHAR(100) NOT NULL COMMENT '渠道名称',
    spread_adjust DECIMAL(16,8) NOT NULL DEFAULT 0 COMMENT '点差调整',
    is_enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    push_interval INT DEFAULT 10 COMMENT '推送间隔(秒)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_channel_code (channel_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牌价发布渠道配置表';

DROP TABLE IF EXISTS t_rate_publish_log;
CREATE TABLE t_rate_publish_log (
    id BIGINT NOT NULL COMMENT '主键ID',
    rate_id BIGINT NOT NULL COMMENT '汇率ID',
    channel_code VARCHAR(30) NOT NULL COMMENT '渠道编码',
    published_rate DECIMAL(16,8) NOT NULL COMMENT '发布牌价',
    publish_time DATETIME NOT NULL COMMENT '发布时间',
    publish_status TINYINT NOT NULL DEFAULT 1 COMMENT '发布状态: 1=成功 0=失败',
    error_msg VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_rate_id (rate_id),
    KEY idx_channel_time (channel_code, publish_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牌价发布日志表';
