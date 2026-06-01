package com.forex.trading.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_trade_lifecycle")
public class TradeLifecyclePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long tradeId;
    private String tradeNo;
    private String eventType;
    private LocalDateTime eventTime;
    private String beforeStatus;
    private String afterStatus;
    private String eventData;
    private Long operatorId;
}
