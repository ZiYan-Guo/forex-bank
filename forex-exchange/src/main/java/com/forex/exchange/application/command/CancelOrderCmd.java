package com.forex.exchange.application.command;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CancelOrderCmd {

    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotBlank(message = "取消原因不能为空")
    private String reason;
}
