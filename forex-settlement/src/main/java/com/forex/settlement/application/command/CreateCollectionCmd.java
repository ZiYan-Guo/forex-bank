package com.forex.settlement.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCollectionCmd {

    private Long customerId;

    @NotBlank
    private String collectionType;

    private String collectionForm;

    @NotNull
    private BigDecimal collectionAmount;

    @NotBlank
    private String collectionCurrency;

    private String draweeName;
    private String documentsList;
    private String remark;
}
