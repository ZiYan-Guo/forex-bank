package com.forex.account.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OpenAccountCmd {

    @NotNull
    private Long customerId;

    @NotBlank
    private String accountType;

    @NotBlank
    private String currency;

    @NotBlank
    private String accountName;

    private String openBranch;
}
