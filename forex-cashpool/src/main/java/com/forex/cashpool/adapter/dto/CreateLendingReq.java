package com.forex.cashpool.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "创建境外放款请求")
public class CreateLendingReq {
    @Schema(description = "本方实体") private String entityId;
    @Schema(description = "境外关联方") private String overseasParty;
    @Schema(description = "放款金额") private BigDecimal amount;
    @Schema(description = "币种") private String currency;
    @Schema(description = "利率") private BigDecimal interestRate;
    @Schema(description = "起息日") private LocalDate startDate;
    @Schema(description = "到期日") private LocalDate maturityDate;
}
