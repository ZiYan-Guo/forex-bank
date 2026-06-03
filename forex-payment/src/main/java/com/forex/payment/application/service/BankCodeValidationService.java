package com.forex.payment.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Slf4j
@Service
public class BankCodeValidationService {

    private static final Pattern SWIFT_PATTERN = Pattern.compile("^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$");

    public boolean validateSwiftCode(String swiftCode) {
        if (swiftCode == null || swiftCode.isBlank()) return false;
        return SWIFT_PATTERN.matcher(swiftCode.toUpperCase()).matches();
    }

    public boolean validateIban(String iban) {
        if (iban == null || iban.length() < 15 || iban.length() > 34) return false;
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        StringBuilder numeric = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isDigit(c)) numeric.append(c);
            else numeric.append(c - 'A' + 10);
        }
        return new java.math.BigInteger(numeric.toString())
                .mod(new java.math.BigInteger("97")).intValue() == 1;
    }

    public String autoCompleteBic(String partialCode, String bankName) {
        return switch (partialCode != null ? partialCode.substring(0, Math.min(4, partialCode.length())) : "") {
            case "BKCH" -> "BKCHCNBJXXX";
            case "ICBK" -> "ICBKCNBJXXX";
            case "ABOC" -> "ABOCCNBJXXX";
            case "MSBC" -> "MSBCCNBJXXX";
            case "CITI" -> "CITIUS33XXX";
            case "HSBC" -> "HSBCHKHHXXX";
            default -> partialCode != null && partialCode.length() >= 8 ? partialCode : null;
        };
    }

    public BigDecimal calculateFee(String channel, String chargeBearer, BigDecimal amount) {
        if (channel == null) return BigDecimal.ZERO;
        BigDecimal baseFee = switch (channel) {
            case "SWIFT" -> new BigDecimal("35");
            case "CIPS" -> new BigDecimal("25");
            case "CFXPS" -> new BigDecimal("15");
            default -> new BigDecimal("20");
        };
        if ("OUR".equals(chargeBearer)) return baseFee.multiply(new BigDecimal("1.5"));
        if ("BEN".equals(chargeBearer)) return BigDecimal.ZERO;
        return baseFee;
    }
}
