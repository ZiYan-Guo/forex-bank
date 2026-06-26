package com.forex.account.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;

import lombok.Getter;

import java.util.Objects;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class AccountNumber extends BaseValueObject {

    private final String accountNo;

    private AccountNumber(String accountNo) {
        if (accountNo == null || accountNo.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "账号不能为空");
        }
        this.accountNo = accountNo;
    }

    public static AccountNumber of(String accountNo) {
        return new AccountNumber(accountNo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountNumber that)) return false;
        return Objects.equals(accountNo, that.accountNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNo);
    }
}
