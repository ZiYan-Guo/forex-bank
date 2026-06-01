package com.forex.saccr.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class SaccrResult extends BaseAggregate {

    private Long id;
    private String calcNo;
    private Long tradeId;
    private String tradeNo;
    private String counterPartyId;
    private LocalDate calcDate;
    private BigDecimal rc = BigDecimal.ZERO;
    private BigDecimal pfe = BigDecimal.ZERO;
    private BigDecimal exposure = BigDecimal.ZERO;
    private BigDecimal alpha = new BigDecimal("1.4");
    private String calcMethod = "SA-CCR";
    private String resultJson;

    private SaccrResult() {
        super();
    }

    public static SaccrResult create(String calcNo, Long tradeId, String tradeNo,
                                      String counterPartyId, LocalDate calcDate) {
        SaccrResult result = new SaccrResult();
        result.calcNo = calcNo;
        result.tradeId = tradeId;
        result.tradeNo = tradeNo;
        result.counterPartyId = counterPartyId;
        result.calcDate = calcDate;
        result.validate();
        return result;
    }

    public static SaccrResult reconstitute(Long id, String calcNo, Long tradeId,
                                            String tradeNo, String counterPartyId,
                                            LocalDate calcDate, BigDecimal rc,
                                            BigDecimal pfe, BigDecimal exposure,
                                            BigDecimal alpha, String calcMethod,
                                            String resultJson) {
        SaccrResult result = new SaccrResult();
        result.id = id;
        result.calcNo = calcNo;
        result.tradeId = tradeId;
        result.tradeNo = tradeNo;
        result.counterPartyId = counterPartyId;
        result.calcDate = calcDate;
        result.rc = rc;
        result.pfe = pfe;
        result.exposure = exposure;
        result.alpha = alpha;
        result.calcMethod = calcMethod;
        result.resultJson = resultJson;
        return result;
    }

    public void updateResult(BigDecimal rc, BigDecimal pfe) {
        this.rc = rc;
        this.pfe = pfe;
        this.exposure = this.alpha.multiply(rc.add(pfe)).setScale(2, java.math.RoundingMode.HALF_UP);
        markUpdated();
    }

    @Override
    protected void validate() {
        if (tradeId == null) {
            throw new BusinessException("交易ID不能为空");
        }
    }
}
