package com.forex.bookkeeping.domain.model.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class JournalQuery extends PageReq {

    private String voucherNo;
    private LocalDate voucherDate;
    private String fiscalPeriod;
    private String bizType;
    private String entryStatus;
    private String accountCode;
    private String entryDirection;
    private String currency;
    private LocalDate startDate;
    private LocalDate endDate;
}
