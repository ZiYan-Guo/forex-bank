package com.forex.clearing.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_settlement_tracker")
public class SettlementTrackerPO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String trackingId;
    private String paymentNo;
    private String instructionNo;
    private String currentStatus;
    private LocalDateTime statusChangedAt;
    private String channel;
    private String gpiStatus;
    private String exceptionReason;
    private String exceptionDetail;
    private LocalDateTime createTime;
}
