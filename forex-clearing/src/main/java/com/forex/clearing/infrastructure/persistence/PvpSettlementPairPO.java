package com.forex.clearing.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_pvp_settlement_pair")
public class PvpSettlementPairPO extends BasePO {

    private String pairId;
    private Long payInstructionId;
    private String payInstructionNo;
    private Long receiveInstructionId;
    private String receiveInstructionNo;
    private String payCurrency;
    private BigDecimal payAmount;
    private String receiveCurrency;
    private BigDecimal receiveAmount;
    private LocalDate settlementDate;
    private String status;
    private String failureReason;
    private LocalDateTime settledAt;
}
