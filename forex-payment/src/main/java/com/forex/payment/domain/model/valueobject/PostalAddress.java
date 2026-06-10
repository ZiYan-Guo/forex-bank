package com.forex.payment.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.util.Objects;

@Getter
public class PostalAddress extends BaseValueObject {
    private final String country;
    private final String province;
    private final String city;
    private final String streetName;
    private final String buildingNumber;
    private final String buildingName;
    private final String postCode;
    private final String department;
    private final String subDepartment;
    private final String townName;

    private PostalAddress(String country, String province, String city, String streetName,
                          String buildingNumber, String buildingName, String postCode,
                          String department, String subDepartment, String townName) {
        this.country = country;
        this.province = province;
        this.city = city;
        this.streetName = streetName;
        this.buildingNumber = buildingNumber;
        this.buildingName = buildingName;
        this.postCode = postCode;
        this.department = department;
        this.subDepartment = subDepartment;
        this.townName = townName;
    }

    public static PostalAddress of(String country, String city, String streetName,
                                   String buildingNumber, String postCode) {
        return new PostalAddress(country, null, city, streetName, buildingNumber, null, postCode, null, null, null);
    }

    public PostalAddress withProvince(String province) {
        return new PostalAddress(country, province, city, streetName, buildingNumber, buildingName, postCode, department, subDepartment, townName);
    }

    public PostalAddress withBuildingName(String buildingName) {
        return new PostalAddress(country, province, city, streetName, buildingNumber, buildingName, postCode, department, subDepartment, townName);
    }

    public PostalAddress withDepartment(String dept, String subDept) {
        return new PostalAddress(country, province, city, streetName, buildingNumber, buildingName, postCode, dept, subDept, townName);
    }

    public PostalAddress withTownName(String townName) {
        return new PostalAddress(country, province, city, streetName, buildingNumber, buildingName, postCode, department, subDepartment, townName);
    }

    public boolean isValidForPayment() {
        return country != null && !country.isBlank()
                && city != null && !city.isBlank()
                && ((streetName != null && !streetName.isBlank()) || (buildingNumber != null && !buildingNumber.isBlank()));
    }

    public String toUnstructured() {
        StringBuilder sb = new StringBuilder();
        if (country != null) sb.append(country).append(", ");
        if (province != null) sb.append(province).append(", ");
        if (city != null) sb.append(city).append(", ");
        if (streetName != null) sb.append(streetName);
        if (buildingNumber != null) sb.append(" ").append(buildingNumber);
        if (buildingName != null) sb.append(", ").append(buildingName);
        if (postCode != null) sb.append(", ").append(postCode);
        return sb.toString().trim();
    }

    public static PostalAddress parse(String unstructured) {
        if (unstructured == null || unstructured.isBlank()) return null;
        String[] parts = unstructured.split(",");
        String country = parts.length > 0 ? parts[0].trim() : null;
        String city = parts.length > 1 ? parts[1].trim() : null;
        String street = parts.length > 2 ? parts[2].trim() : null;
        return new PostalAddress(country, null, city, street, null, null, null, null, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostalAddress that)) return false;
        return Objects.equals(country, that.country) && Objects.equals(city, that.city)
                && Objects.equals(streetName, that.streetName) && Objects.equals(postCode, that.postCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, city, streetName, postCode);
    }
}
