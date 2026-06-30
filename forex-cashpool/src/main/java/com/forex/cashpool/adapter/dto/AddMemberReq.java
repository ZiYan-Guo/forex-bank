package com.forex.cashpool.adapter.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "AddMemberReq")
public class AddMemberReq {
    @Schema(description = "poolId") private String poolId;
    @Schema(description = "memberAccountId") private Long memberAccountId;
    @Schema(description = "memberType") private String memberType;
    @Schema(description = "currency") private String currency;
    @Schema(description = "settlementMode") private String settlementMode;
    @Schema(description = "contributionLimit") private BigDecimal contributionLimit;
}
