package com.forex.clearing.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * CFETS trade confirmation DTO.
 * CFETS 交易确认数据。
 */
@Data
@AllArgsConstructor
public class TradeConfirmation {
    private String refNo;
    private String currencyPair;
    private String direction;
    private BigDecimal amount;
    private BigDecimal rate;
    private LocalDate valueDate;
    private String counterparty;
}
