package com.forex.cashpool.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "添加资金池成员请求")
public class AddMemberReq {
    @Schema(description = "资金池ID") private String poolId;
    @Schema(description = "成员ID") private String memberId;
    @Schema(description = "币种") private String currency;
}
