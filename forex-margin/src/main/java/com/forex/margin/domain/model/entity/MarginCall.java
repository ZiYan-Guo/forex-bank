package com.forex.margin.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarginCall extends BaseEntity {

    private Long id;
    private Long marginId;
    private String marginNo;
    private String callType;
    private BigDecimal callAmount;
    private LocalDateTime callDate;
    private LocalDateTime responseDate;
    private String responseStatus;

    public void respond(String status) {
        this.responseStatus = status;
        this.responseDate = LocalDateTime.now();
    }
}
