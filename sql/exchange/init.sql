-- ============================================
-- 结售汇业务模块 - 数据库初始化脚本
-- ============================================
USE forex_exchange;

DROP TABLE IF EXISTS t_exchange_order;
CREATE TABLE t_exchange_order (
    id BIGINT NOT NULL COMMENT '主键ID',
    order_no VARCHAR(64) NOT NULL COMMENT '订单编号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    order_type VARCHAR(30) NOT NULL COMMENT '订单类型: SPOT=即期 FORWARD=远期 PENDING_ORDER=挂单 OPTION_DATE=择期',
    deal_type VARCHAR(20) NOT NULL COMMENT '交易方向: BUY=结汇 SELL=售汇',
    base_currency VARCHAR(10) NOT NULL COMMENT '基础货币(客户持有)',
    quote_currency VARCHAR(10) NOT NULL COMMENT '报价货币(客户需要)',
    order_amount DECIMAL(22,6) NOT NULL COMMENT '订单金额(基础货币)',
    settle_amount DECIMAL(22,6) DEFAULT NULL COMMENT '结算金额(报价货币)',
    bid_rate DECIMAL(16,8) DEFAULT NULL COMMENT '买入价',
    ask_rate DECIMAL(16,8) DEFAULT NULL COMMENT '卖出价',
    confirmed_rate DECIMAL(16,8) DEFAULT NULL COMMENT '成交汇率',
    rate_type VARCHAR(20) DEFAULT 'REALTIME' COMMENT '汇率类型: FIXED=固定 REALTIME=实时',
    lock_rate_time DATETIME DEFAULT NULL COMMENT '锁价时间',
    lock_rate_expire_time DATETIME DEFAULT NULL COMMENT '锁价过期时间',
    value_date DATE DEFAULT NULL COMMENT '交割日',
    maturity_date DATE DEFAULT NULL COMMENT '到期日(远期/择期)',
    order_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态: PENDING=待确认 CONFIRMED=已确认 PROCESSING=处理中 SUCCESS=成功 FAILED=失败 CANCELLED=已取消 REVERSED=已冲正',
    customer_account_no VARCHAR(32) DEFAULT NULL COMMENT '客户账户号',
    bank_account_no VARCHAR(32) DEFAULT NULL COMMENT '银行账户号',
    fee_amount DECIMAL(22,6) DEFAULT 0 COMMENT '手续费',
    commission_amount DECIMAL(22,6) DEFAULT 0 COMMENT '佣金',
    settlement_type VARCHAR(20) DEFAULT 'T2' COMMENT '结算方式: T0/T1/T2',
    channel VARCHAR(30) DEFAULT 'COUNTER' COMMENT '渠道: COUNTER=柜面 EBANK=企业网银 MBANK=手机银行 API=银企直连',
    operator_id BIGINT DEFAULT NULL COMMENT '操作员ID',
    remark VARCHAR(1000) DEFAULT NULL COMMENT '备注',
    cancel_reason VARCHAR(500) DEFAULT NULL COMMENT '取消/冲正原因',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号(乐观锁)',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_customer_id (customer_id),
    KEY idx_order_status (order_status),
    KEY idx_create_time (create_time),
    KEY idx_value_date (value_date),
    KEY idx_customer_status (customer_id, order_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结售汇订单表';

DROP TABLE IF EXISTS t_exchange_quote;
CREATE TABLE t_exchange_quote (
    id BIGINT NOT NULL COMMENT '主键ID',
    customer_id BIGINT DEFAULT NULL COMMENT '客户ID(为空表示公开牌价)',
    base_currency VARCHAR(10) NOT NULL COMMENT '基础货币',
    quote_currency VARCHAR(10) NOT NULL COMMENT '报价货币',
    bid_rate DECIMAL(16,8) NOT NULL COMMENT '买入价',
    ask_rate DECIMAL(16,8) NOT NULL COMMENT '卖出价',
    mid_rate DECIMAL(16,8) DEFAULT NULL COMMENT '中间价',
    quote_time DATETIME NOT NULL COMMENT '报价时间',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    quote_status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1=有效 0=无效',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_customer_currency (customer_id, base_currency, quote_currency),
    KEY idx_quote_time (quote_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结售汇报价记录表';

DROP TABLE IF EXISTS t_exchange_position;
CREATE TABLE t_exchange_position (
    id BIGINT NOT NULL COMMENT '主键ID',
    currency VARCHAR(10) NOT NULL COMMENT '币种',
    open_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '开盘头寸',
    current_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '当前头寸',
    buy_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '当日买入累计',
    sell_amount DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '当日卖出累计',
    position_date DATE NOT NULL COMMENT '头寸日期',
    position_status VARCHAR(20) DEFAULT 'OPEN' COMMENT '状态: OPEN=未平盘 CLOSED=已平盘',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_currency_date (currency, position_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结售汇敞口表';
