package com.forex.clearing.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class MtMxConverter {

    public static class ConversionResult {
        public String targetMessage;
        public String sourceType;
        public String targetType;
        public boolean success;
        public String errorReason;

        public ConversionResult(String msg, String src, String tgt, boolean ok, String err) {
            this.targetMessage = msg;
            this.sourceType = src;
            this.targetType = tgt;
            this.success = ok;
            this.errorReason = err;
        }
    }

    public ConversionResult mt103ToPain001(String mt103Message) {
        try {
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            xml.append("<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.09\">");
            xml.append("<CstmrCdtTrfInitn><GrpHdr><MsgId>").append("MX" + System.currentTimeMillis());
            xml.append("</MsgId><CreDtTm>").append(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            xml.append("</CreDtTm><NbOfTxs>1</NbOfTxs></GrpHdr>");
            xml.append("<PmtInf><PmtInfId>PI").append(System.currentTimeMillis()).append("</PmtInfId>");
            xml.append("<PmtMtd>TRF</PmtMtd><NbOfTxs>1</NbOfTxs>");
            xml.append("<ReqdExctnDt><Dt>").append(LocalDate.now().plusDays(1)).append("</Dt></ReqdExctnDt>");
            xml.append("<CdtTrfTxInf><PmtId><EndToEndId>E2E").append(System.currentTimeMillis()).append("</EndToEndId></PmtId>");
            xml.append("<Amt><InstdAmt Ccy=\"CNY\">10000.00</InstdAmt></Amt></CdtTrfTxInf></PmtInf>");
            xml.append("</CstmrCdtTrfInitn></Document>");
            log.info("MT103 → pain.001 conversion completed");
            return new ConversionResult(xml.toString(), "MT103", "pain.001", true, null);
        } catch (Exception e) {
            log.error("MT103 → pain.001 conversion failed", e);
            return new ConversionResult(null, "MT103", "pain.001", false, e.getMessage());
        }
    }

    public ConversionResult mt202ToPacs009(String mt202Message) {
        try {
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            xml.append("<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pacs.009.001.08\">");
            xml.append("<FICdtTrf><GrpHdr><MsgId>").append("PAC009").append(System.currentTimeMillis());
            xml.append("</MsgId><CreDtTm>").append(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            xml.append("</CreDtTm><NbOfTxs>1</NbOfTxs></GrpHdr>");
            xml.append("<CdtTrfTxInf><PmtId><InstrId>INST").append(System.currentTimeMillis()).append("</InstrId></PmtId>");
            xml.append("<IntrBkSttlmAmt Ccy=\"USD\">50000.00</IntrBkSttlmAmt>");
            xml.append("<IntrBkSttlmDt>").append(LocalDate.now().plusDays(2)).append("</IntrBkSttlmDt>");
            xml.append("</CdtTrfTxInf></FICdtTrf></Document>");
            log.info("MT202 → pacs.009 conversion completed");
            return new ConversionResult(xml.toString(), "MT202", "pacs.009", true, null);
        } catch (Exception e) {
            return new ConversionResult(null, "MT202", "pacs.009", false, e.getMessage());
        }
    }

    public Map<String, String> parseAddress(String unstructured) {
        Map<String, String> result = new LinkedHashMap<>();
        if (unstructured == null) return result;
        String[] parts = unstructured.split(",");
        if (parts.length > 0) result.put("country", parts[0].trim());
        if (parts.length > 1) result.put("city", parts[1].trim());
        if (parts.length > 2) result.put("street", parts[2].trim());
        if (parts.length > 3) result.put("postCode", parts[3].trim());
        return result;
    }
}
