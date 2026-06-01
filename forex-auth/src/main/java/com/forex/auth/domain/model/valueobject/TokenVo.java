package com.forex.auth.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class TokenVo extends BaseValueObject {

    private final String accessToken;
    private final String refreshToken;
    private final long expiresIn;

    public TokenVo(String accessToken, String refreshToken, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenVo that)) return false;
        return Objects.equals(accessToken, that.accessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken);
    }
}
