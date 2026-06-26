package com.forex.risk.domain.model.valueobject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forex.common.base.domain.BaseValueObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RuleCondition extends BaseValueObject {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String type;
    private String field;
    private String operator;
    private BigDecimal value;
    private String currency;
    private String pattern;
    private Integer windowMinutes;

    public static RuleCondition fromJson(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, RuleCondition.class);
        } catch (Exception e) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "Failed to parse RuleCondition from JSON: " + json, e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuleCondition that)) return false;
        return Objects.equals(type, that.type)
                && Objects.equals(field, that.field)
                && Objects.equals(operator, that.operator)
                && Objects.equals(value, that.value)
                && Objects.equals(currency, that.currency)
                && Objects.equals(pattern, that.pattern)
                && Objects.equals(windowMinutes, that.windowMinutes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, field, operator, value, currency, pattern, windowMinutes);
    }
}
