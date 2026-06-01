package com.forex.bookkeeping.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_journal_entry")
public class JournalEntryPO extends BasePO {

    private String voucherNo;
    private LocalDate voucherDate;
    private String fiscalPeriod;
    private String bizType;
    private String bizNo;
    private String currency;
    private BigDecimal amount;
    private String entryDirection;
    private String accountCode;
    private String accountName;
    private String oppositeAccountCode;
    private String summary;
    private String entryStatus;
    private String reversedVoucherNo;
    private LocalDateTime postedTime;
    private Long operatorId;
}
