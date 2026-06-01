package com.forex.auth.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;

import cn.hutool.crypto.digest.BCrypt;

import lombok.Getter;

import java.util.Objects;

@Getter
public class PasswordHash extends BaseValueObject {

    private final String hash;

    private PasswordHash(String hash) {
        this.hash = hash;
    }

    public static PasswordHash encode(String rawPassword) {
        return new PasswordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
    }

    public static PasswordHash fromHash(String hash) {
        return new PasswordHash(hash);
    }

    public boolean matches(String rawPassword) {
        return BCrypt.checkpw(rawPassword, hash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordHash that)) return false;
        return Objects.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }
}
