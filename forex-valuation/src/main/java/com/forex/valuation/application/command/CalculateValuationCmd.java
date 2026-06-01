package com.forex.valuation.application.command;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CalculateValuationCmd {

    private Long tradeId;

    private LocalDate valuationDate;
}
