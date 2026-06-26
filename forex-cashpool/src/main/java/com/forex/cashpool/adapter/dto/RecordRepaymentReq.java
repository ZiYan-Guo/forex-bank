package com.forex.cashpool.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "还款请求")
public class RecordRepaymentReq {
    @Schema(description = "还款金额") private BigDecimal amount;
}
