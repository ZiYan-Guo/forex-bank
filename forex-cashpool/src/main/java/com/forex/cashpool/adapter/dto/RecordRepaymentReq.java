package com.forex.cashpool.adapter.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "RecordRepaymentReq")
public class RecordRepaymentReq {
    @Schema(description = "amount") private BigDecimal amount;
}
