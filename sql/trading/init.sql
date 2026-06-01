-- ============================================
-- 外汇买卖与衍生交易模块 - 数据库初始化脚本
-- ============================================
USE forex_trading;

DROP TABLE IF EXISTS t_fx_trade;
CREATE TABLE t_fx_trade (
    id BIGINT NOT NULL COMMENT '主键ID',
    trade_no VARCHAR(64) NOT NULL COMMENT '交易编号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    trade_type VARCHAR(30) NOT NULL COMMENT '交易类型: SPOT=即期外汇 FORWARD=远期外汇 SWAP=掉期 OPTION=期权',
    deal_type VARCHAR(20) NOT NULL COMMENT '买卖方向: BUY=买入 SELL=卖出',
    buy_currency VARCHAR(10) NOT NULL COMMENT '买入币种',
    sell_currency VARCHAR(10) NOT NULL COMMENT '卖出币种',
    buy_amount DECIMAL(22,6) DEFAULT NULL COMMENT '买入金额',
    sell_amount DECIMAL(22,6) DEFAULT NULL COMMENT '卖出金额',
    trade_rate DECIMAL(16,8) NOT NULL COMMENT '成交汇率',
    value_date DATE NOT NULL COMMENT '交割日',
    maturity_date DATE DEFAULT NULL COMMENT '到期日(远期/掉期)',
    near_value_date DATE DEFAULT NULL COMMENT '近期交割日(掉期)',
    far_value_date DATE DEFAULT NULL COMMENT '远期交割日(掉期)',
    near_rate DECIMAL(16,8) DEFAULT NULL COMMENT '近期汇率(掉期)',
    far_rate DECIMAL(16,8) DEFAULT NULL COMMENT '远期汇率(掉期)',
    swap_points DECIMAL(16,8) DEFAULT NULL COMMENT '掉期点',
    option_type VARCHAR(20) DEFAULT NULL COMMENT '期权类型: CALL=看涨 PUT=看跌',
    strike_price DECIMAL(16,8) DEFAULT NULL COMMENT '行权价格',
    premium_amount DECIMAL(22,6) DEFAULT NULL COMMENT '期权费',
    premium_currency VARCHAR(10) DEFAULT NULL COMMENT '期权费币种',
    premium_date DATE DEFAULT NULL COMMENT '期权费支付日',
    expiry_date DATE DEFAULT NULL COMMENT '期权到期日',
    delivery_type VARCHAR(20) DEFAULT 'PHYSICAL' COMMENT '交割方式: PHYSICAL=实物 CASH=现金差额',
    trade_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING=待确认 CONFIRMED=已确认 EXECUTED=已执行 CANCELLED=已取消 SETTLED=已结算 ROLLED=已展期 CLOSED=已平仓 EXPIRED=已到期(期权)',
    settlement_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '清算状态: PENDING=待清算 SETTLED=已清算',
    nostro_account VARCHAR(30) DEFAULT NULL COMMENT '往来账户',
    counterparty VARCHAR(200) DEFAULT NULL COMMENT '交易对手',
    trade_channel VARCHAR(30) DEFAULT 'COUNTER' COMMENT '交易渠道',
    operator_id BIGINT DEFAULT NULL COMMENT '交易员ID',
    confirm_time DATETIME DEFAULT NULL COMMENT '确认时间',
    execute_time DATETIME DEFAULT NULL COMMENT '执行时间',
    settle_time DATETIME DEFAULT NULL COMMENT '清算时间',
    remark VARCHAR(1000) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_trade_no (trade_no),
    KEY idx_customer_id (customer_id),
    KEY idx_trade_status (trade_status),
    KEY idx_value_date (value_date),
    KEY idx_maturity_date (maturity_date),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外汇交易表';

DROP TABLE IF EXISTS t_trade_position;
CREATE TABLE t_trade_position (
    id BIGINT NOT NULL COMMENT '主键ID',
    position_no VARCHAR(64) NOT NULL COMMENT '头寸编号',
    currency_pair VARCHAR(20) NOT NULL COMMENT '货币对',
    buy_currency VARCHAR(10) NOT NULL,
    sell_currency VARCHAR(10) NOT NULL,
    net_position DECIMAL(22,6) NOT NULL DEFAULT 0 COMMENT '净头寸(买入-卖出,以买入币种计)',
    position_date DATE NOT NULL COMMENT '头寸日期',
    position_type VARCHAR(30) NOT NULL COMMENT '类型: FX_SPOT=即期敞口 FX_FORWARD=远期敞口 OPTION_DELTA=期权Delta敞口',
    open_trades INT DEFAULT 0 COMMENT '在途交易数量',
    mark_to_market DECIMAL(22,6) DEFAULT NULL COMMENT '盯市估值',
    valuation_rate DECIMAL(16,8) DEFAULT NULL COMMENT '估值汇率',
    position_status VARCHAR(20) DEFAULT 'OPEN' COMMENT '状态: OPEN=未平盘 HEDGED=已对冲 CLOSED=已平盘',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_position_date_pair_type (position_date, currency_pair, position_type),
    KEY idx_position_date (position_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易头寸表';

DROP TABLE IF EXISTS t_trade_lifecycle;
CREATE TABLE t_trade_lifecycle (
    id BIGINT NOT NULL COMMENT '主键ID',
    trade_id BIGINT NOT NULL COMMENT '交易ID',
    trade_no VARCHAR(64) NOT NULL COMMENT '交易编号',
    event_type VARCHAR(30) NOT NULL COMMENT '事件类型: CREATED=创建 CONFIRMED=确认 EXECUTED=执行 ROLL_OVER=展期 CLOSE=平仓 SETTLED=结算 CANCELLED=取消 EXPIRED=到期 EXERCISE=行权',
    event_time DATETIME NOT NULL COMMENT '事件时间',
    before_status VARCHAR(30) DEFAULT NULL COMMENT '变更前状态',
    after_status VARCHAR(30) NOT NULL COMMENT '变更后状态',
    event_data JSON DEFAULT NULL COMMENT '事件附加数据',
    operator_id BIGINT DEFAULT NULL COMMENT '操作人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_trade_id (trade_id),
    KEY idx_trade_no (trade_no),
    KEY idx_event_time (event_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易生命周期表';
