package com.forex.clearing.application.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SWIFT message generator for MT and ISO 20022 formats.
 * SWIFT 报文生成器，支持 MT 和 ISO 20022 格式。
 */
@Slf4j
@Service
public class SwiftMessageGenerator {

    /**
     * Generate MT 300: Foreign Exchange Confirmation.
     * 生成 MT 300 外汇交易确认报文。
     */
    public String generateMT300(String tradeNo, String boughtCcy, BigDecimal boughtAmt,
                                 String soldCcy, BigDecimal soldAmt, BigDecimal rate,
                                 LocalDate valueDate, String counterpartyBic) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyMMdd");
        StringBuilder msg = new StringBuilder();
        msg.append("{1:F01YOURBANKHKAXFXXXX0000000000}\n");
        msg.append("{2:O3001130").append(df.format(LocalDate.now()))
                .append("OURBANKHKAXFXXXX0000000000")
                .append(df.format(LocalDate.now())).append("N}\n");
        msg.append("{4:\n");
        msg.append(":15A:NEW CONFIRMATION\n");
        msg.append(":20:").append(tradeNo).append("\n");
        msg.append(":22A:NEW\n");
        msg.append(":32B:").append(boughtCcy)
                .append(String.format("%015.2f", boughtAmt)).append("\n");
        msg.append(":33B:").append(soldCcy)
                .append(String.format("%015.2f", soldAmt)).append("\n");
        msg.append(":36:").append(rate.toPlainString()).append("\n");
        msg.append(":30V:").append(df.format(valueDate)).append("\n");
        msg.append(":57A:").append(counterpartyBic != null ? counterpartyBic : "//XX0000000").append("\n");
        msg.append("-}");
        log.info("Generated MT300 for trade: {}", tradeNo);
        return msg.toString();
    }

    /**
     * Generate MT 202: General Financial Institution Transfer.
     * 生成 MT 202 银行间头寸调拨报文。
     */
    public String generateMT202(String refNo, String currency, BigDecimal amount,
                                 LocalDate valueDate, String senderBic, String receiverBic) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyMMdd");
        StringBuilder msg = new StringBuilder();
        msg.append("{1:F01").append(senderBic).append("XXXX0000000000}\n");
        msg.append("{2:O202").append(df.format(LocalDate.now()))
                .append(receiverBic).append("XXXX0000000000")
                .append(df.format(LocalDate.now())).append("}\n");
        msg.append("{4:\n");
        msg.append(":20:").append(refNo).append("\n");
        msg.append(":21:").append(refNo).append("\n");
        msg.append(":32A:").append(df.format(valueDate)).append(currency)
                .append(String.format("%015.2f", amount)).append("\n");
        msg.append(":57A:").append(receiverBic).append("\n");
        msg.append("-}");
        log.info("Generated MT202 for ref: {}", refNo);
        return msg.toString();
    }

    /**
     * Generate ISO 20022 pacs.008: Customer Credit Transfer.
     * 生成 ISO 20022 pacs.008 客户汇款报文。
     */
    public String generatePacs008(String msgId, String debtorName, String debtorAcct,
                                   String creditorName, String creditorAcct, String creditorBic,
                                   String currency, BigDecimal amount, String remittanceInfo) {
        String xml = String.format("""
            <?xml version="1.0" encoding="UTF-8"?>
            <Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08">
              <FIToFICstmrCdtTrf>
                <GrpHdr>
                  <MsgId>%s</MsgId>
                  <CreDtTm>%s</CreDtTm>
                  <NbOfTxs>1</NbOfTxs>
                </GrpHdr>
                <CdtTrfTxInf>
                  <PmtId><EndToEndId>%s</EndToEndId></PmtId>
                  <IntrBkSttlmAmt Ccy="%s">%s</IntrBkSttlmAmt>
                  <Dbtr><Nm>%s</Nm></Dbtr>
                  <DbtrAcct><Id><Othr><Id>%s</Id></Othr></Id></DbtrAcct>
                  <Cdtr><Nm>%s</Nm></Cdtr>
                  <CdtrAcct><Id><Othr><Id>%s</Id></Othr></Id></CdtrAcct>
                  <CdtrAgt><FinInstnId><BICFI>%s</BICFI></FinInstnId></CdtrAgt>
                  <RmtInf><Ustrd>%s</Ustrd>
                </CdtTrfTxInf>
              </FIToFICstmrCdtTrf>
            </Document>""",
            msgId, LocalDateTime.now(), msgId, currency, amount.toPlainString(),
            debtorName, debtorAcct, creditorName, creditorAcct, creditorBic, remittanceInfo);
        log.info("Generated pacs.008 for msgId: {}", msgId);
        return xml;
    }
}
