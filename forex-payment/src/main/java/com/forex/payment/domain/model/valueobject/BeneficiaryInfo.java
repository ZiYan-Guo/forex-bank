package com.forex.payment.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.util.Objects;

@Getter
public class BeneficiaryInfo extends BaseValueObject {

    private final String name;
    private final String accountNo;
    private final String bankName;
    private final String bankSwiftCode;
    private final PostalAddress postalAddress;
    private final String country;

    private BeneficiaryInfo(String name, String accountNo, String bankName,
                            String bankSwiftCode, PostalAddress postalAddress, String country) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("收款人名称不能为空");
        }
        if (accountNo == null || accountNo.isBlank()) {
            throw new IllegalArgumentException("收款人账号不能为空");
        }
        this.name = name;
        this.accountNo = accountNo;
        this.bankName = bankName;
        this.bankSwiftCode = bankSwiftCode;
        this.postalAddress = postalAddress;
        this.country = country;
    }

    public static BeneficiaryInfo of(String name, String accountNo, String bankName,
                                      String bankSwiftCode, PostalAddress postalAddress, String country) {
        return new BeneficiaryInfo(name, accountNo, bankName, bankSwiftCode, postalAddress, country);
    }

    public String getAddressLine() {
        return postalAddress != null ? postalAddress.toUnstructured() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeneficiaryInfo that)) return false;
        return Objects.equals(name, that.name)
                && Objects.equals(accountNo, that.accountNo)
                && Objects.equals(bankName, that.bankName)
                && Objects.equals(bankSwiftCode, that.bankSwiftCode)
                && Objects.equals(postalAddress, that.postalAddress)
                && Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, accountNo, bankName, bankSwiftCode, postalAddress, country);
    }

    @Override
    public String toString() {
        return "BeneficiaryInfo(name=" + name + ", accountNo=" + accountNo + ")";
    }
}
