package com.forex.clearing.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * CIPS (Cross-border Interbank Payment System) message generator.
 * Generates ISO 20022-based CIPS standard messages for cross-border RMB payments.
 * CIPS 跨境银行间支付系统报文生成器。生成基于 ISO 20022 的 CIPS 标准报文。
 */
@Slf4j
@Service
public class CipsMessageGenerator {

    /**
     * Generate CIPS.111 Customer Credit Transfer (pacs.008 equivalent).
     * 生成 CIPS.111 客户汇款报文（对应 pacs.008）。
     */
    public String generateCips111(String msgId, String debtorName, String debtorAcct,
                                   String creditorName, String creditorAcct, String creditorCipsId,
                                   String currency, BigDecimal amount, String remittanceInfo) {
        log.info("Generating CIPS.111 message: msgId={}, amount={} {}, creditor={}",
                msgId, amount, currency, creditorName);
        String xml = String.format("""
            <?xml version="1.0" encoding="UTF-8"?>
            <Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08">
              <FIToFICstmrCdtTrf>
                <GrpHdr>
                  <MsgId>%s</MsgId>
                  <CreDtTm>%s</CreDtTm>
                  <NbOfTxs>1</NbOfTxs>
                  <SttlmMtd>CLRG</SttlmMtd>
                </GrpHdr>
                <CdtTrfTxInf>
                  <PmtId><EndToEndId>%s</EndToEndId></PmtId>
                  <IntrBkSttlmAmt Ccy="%s">%s</IntrBkSttlmAmt>
                  <IntrBkSttlmDt>%s</IntrBkSttlmDt>
                  <ChrgBr>SLEV</ChrgBr>
                  <Dbtr><Nm>%s</Nm></Dbtr>
                  <DbtrAcct><Id><Othr><Id>%s</Id></Othr></Id></DbtrAcct>
                  <Cdtr><Nm>%s</Nm></Cdtr>
                  <CdtrAcct><Id><Othr><Id>%s</Id></Othr></Id></CdtrAcct>
                  <CdtrAgt><FinInstnId><ClrSysMmbId><MmbId>%s</MmbId></ClrSysMmbId></FinInstnId></CdtrAgt>
                  <RmtInf><Ustrd>%s</Ustrd></RmtInf>
                </CdtTrfTxInf>
              </FIToFICstmrCdtTrf>
            </Document>""",
            msgId, LocalDateTime.now(), msgId, currency, amount.toPlainString(),
            LocalDate.now().plusDays(1), debtorName, debtorAcct, creditorName,
            creditorAcct, creditorCipsId, remittanceInfo != null ? remittanceInfo : "");
        log.debug("CIPS.111 generated successfully: msgId={}, length={}", msgId, xml.length());
        return xml;
    }

    /**
     * Generate CIPS.112 Payment Status Report (pacs.002 equivalent).
     * 生成 CIPS.112 支付状态报告（对应 pacs.002）。
     */
    public String generateCips112(String originalMsgId, String status, String reason) {
        log.info("Generating CIPS.112 status report: originalMsgId={}, status={}", originalMsgId, status);
        String xml = String.format("""
            <?xml version="1.0" encoding="UTF-8"?>
            <Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.002.001.10">
              <FIToFIPmtStsRpt>
                <GrpHdr><MsgId>%s</MsgId><CreDtTm>%s</CreDtTm></GrpHdr>
                <OrgnlGrpInf><OrgnlMsgId>%s</OrgnlMsgId></OrgnlGrpInf>
                <TxInfAndSts>
                  <OrgnlTxId>%s</OrgnlTxId>
                  <TxSts>%s</TxSts>
                  <StsRsnInf><Rsn><Cd>%s</Cd></Rsn></StsRsnInf>
                </TxInfAndSts>
              </FIToFIPmtStsRpt>
            </Document>""",
            UUID.randomUUID().toString(), LocalDateTime.now(), originalMsgId,
            originalMsgId, status, reason != null ? reason : "AC01");
        log.debug("CIPS.112 generated: status={}, reason={}", status, reason);
        return xml;
    }

    /**
     * Generate CIPS.113 Payment Return (pacs.004 equivalent).
     * 生成 CIPS.113 退汇报文（对应 pacs.004）。
     */
    public String generateCips113(String originalMsgId, String returnReason, BigDecimal returnAmount) {
        log.info("Generating CIPS.113 return: originalMsgId={}, amount={}, reason={}",
                originalMsgId, returnAmount, returnReason);
        String xml = String.format("""
            <?xml version="1.0" encoding="UTF-8"?>
            <Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.004.001.09">
              <PmtRtr>
                <GrpHdr><MsgId>%s</MsgId><CreDtTm>%s</CreDtTm><NbOfTxs>1</NbOfTxs></GrpHdr>
                <TxInf>
                  <RtrId>%s</RtrId>
                  <OrgnlTxId>%s</OrgnlTxId>
                  <RtrdIntrBkSttlmAmt Ccy="CNY">%s</RtrdIntrBkSttlmAmt>
                  <RtrRsnInf><Rsn><Cd>%s</Cd></Rsn></RtrRsnInf>
                </TxInf>
              </PmtRtr>
            </Document>""",
            UUID.randomUUID().toString(), LocalDateTime.now(), UUID.randomUUID().toString(),
            originalMsgId, returnAmount != null ? returnAmount.toPlainString() : "0.00", returnReason);
        log.debug("CIPS.113 generated for original: {}", originalMsgId);
        return xml;
    }
}
