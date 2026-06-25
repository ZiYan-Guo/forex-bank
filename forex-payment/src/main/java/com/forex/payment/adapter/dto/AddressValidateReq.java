package com.forex.payment.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "结构化地址验证请求")
public class AddressValidateReq {

    @Schema(description = "国家", example = "CN")
    private String country;

    @Schema(description = "城市", example = "Beijing")
    private String city;

    @Schema(description = "街道名称", example = "Chaoyang Road")
    private String streetName;

    @Schema(description = "楼栋门牌号", example = "100")
    private String buildingNumber;

    @Schema(description = "邮政编码")
    private String postCode;
}
