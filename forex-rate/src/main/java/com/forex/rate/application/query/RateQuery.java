package com.forex.rate.application.query;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RateQuery {

    private String currencyPair;
    private LocalDate rateDate;
}
