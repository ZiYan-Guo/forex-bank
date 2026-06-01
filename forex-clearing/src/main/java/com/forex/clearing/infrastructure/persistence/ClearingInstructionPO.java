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
@TableName("t_clearing_instruction")
public class ClearingInstructionPO extends BasePO {

    private String instructionNo;
    private String bizType;
    private String bizNo;
    private String clearingChannel;
    private String nostroAccount;
    private String counterPartyAccount;
    private String payCurrency;
    private BigDecimal payAmount;
    private String receiveCurrency;
    private BigDecimal receiveAmount;
    private LocalDate valueDate;
    private LocalDate settlementDate;
    private String settlementType;
    private String instructionStatus;
    private String swiftRef;
    private String cipsRef;
    private BigDecimal nostroBalanceBefore;
    private BigDecimal nostroBalanceAfter;
    private LocalDateTime sendTime;
    private LocalDateTime ackTime;
    private LocalDateTime settleTime;
    private Long operatorId;
    private String remark;
}
