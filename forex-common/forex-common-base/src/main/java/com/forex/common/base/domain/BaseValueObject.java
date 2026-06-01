package com.forex.common.base.domain;

import java.io.Serializable;

public abstract class BaseValueObject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
