package com.forex.clearing.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Internal trade DTO for reconciliation matching.
 * 内部交易数据，用于对账匹配。
 */
@Data
@AllArgsConstructor
public class InternalTrade {
    private String tradeNo;
    private String currencyPair;
    private BigDecimal amount;
    private LocalDate valueDate;
    private String counterparty;
}
