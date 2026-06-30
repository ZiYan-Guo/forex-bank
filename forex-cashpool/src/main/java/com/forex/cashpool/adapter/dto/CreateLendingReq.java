package com.forex.cashpool.adapter.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CreateLendingReq")
public class CreateLendingReq {
    @Schema(description = "customerId") private Long customerId;
    @Schema(description = "loanAmount") private BigDecimal loanAmount;
    @Schema(description = "loanCurrency") private String loanCurrency;
    @Schema(description = "interestRate") private BigDecimal interestRate;
    @Schema(description = "repaymentMethod") private String repaymentMethod;
    @Schema(description = "poolId") private Long poolId;
}
