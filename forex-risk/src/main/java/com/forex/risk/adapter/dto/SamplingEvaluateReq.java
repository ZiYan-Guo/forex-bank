package com.forex.risk.adapter.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Sampling rule evaluation request / 抽查规则评估请求")
public class SamplingEvaluateReq {
    @Schema(description = "Customer ID / 客户ID")
    private Long customerId;

    @Schema(description = "Business type / 业务类型")
    private String bizType;

    @Schema(description = "Transaction amount / 交易金额")
    private BigDecimal amount;

    @Schema(description = "Transaction currency / 交易币种")
    private String currency;

    @Schema(description = "Counterparty country code / 交易对手国家代码")
    private String countryCode;

    @Schema(description = "Customer account age in days / 客户开户天数")
    private Integer accountAgeDays;
}
