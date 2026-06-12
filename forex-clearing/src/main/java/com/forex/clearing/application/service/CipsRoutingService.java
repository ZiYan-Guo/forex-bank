package com.forex.clearing.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CIPS participant routing service.
 * Maps BIC codes to CIPS participant IDs and determines routing paths.
 * CIPS 参与者路由服务。BIC↔CIPS ID 映射及路由路径确定。
 */
@Slf4j
@Service
public class CipsRoutingService {

    /** BIC → CIPS Participant ID mapping. BIC到CIPS参与者ID映射。 */
    private final Map<String, String> bicToCipsId = new ConcurrentHashMap<>();
    /** Bank name → CIPS Participant ID mapping. 银行名称映射。 */
    private final Map<String, String> bankNameToCipsId = new ConcurrentHashMap<>();

    public CipsRoutingService() {
        // Initialize with major CIPS participant banks. 初始化主要CIPS参与行。
        bicToCipsId.put("BKCHCNBJ", "CIPS00001");
        bicToCipsId.put("ICBKCNBJ", "CIPS00002");
        bicToCipsId.put("ABOCCNBJ", "CIPS00003");
        bicToCipsId.put("MSBCCNBJ", "CIPS00004");
        bicToCipsId.put("PCBCCNBJ", "CIPS00005");
        bicToCipsId.put("BOFACN3X", "CIPS00010");
        bicToCipsId.put("CITICNSX", "CIPS00011");
        bicToCipsId.put("HSBCCNSH", "CIPS00012");
        bankNameToCipsId.put("Bank of China", "CIPS00001");
        bankNameToCipsId.put("ICBC", "CIPS00002");
        bankNameToCipsId.put("Agricultural Bank of China", "CIPS00003");
        bankNameToCipsId.put("China Construction Bank", "CIPS00004");
        log.info("CIPS routing service initialized with {} BIC mappings and {} bank name mappings",
                bicToCipsId.size(), bankNameToCipsId.size());
    }

    /**
     * Resolve CIPS participant ID from BIC code.
     * 根据 BIC 码解析 CIPS 参与者 ID。
     */
    public String resolveByBic(String bicCode) {
        log.debug("Resolving CIPS ID for BIC: {}", bicCode);
        if (bicCode == null) return null;
        String cipsId = bicToCipsId.get(bicCode.toUpperCase());
        if (cipsId != null) {
            log.debug("CIPS ID resolved: BIC={} → {}", bicCode, cipsId);
        } else {
            log.warn("No CIPS participant found for BIC: {}", bicCode);
        }
        return cipsId;
    }

    /** Resolve by bank name. 按银行名称解析。 */
    public String resolveByBankName(String bankName) {
        log.debug("Resolving CIPS ID for bank: {}", bankName);
        return bankName != null ? bankNameToCipsId.get(bankName) : null;
    }

    /** Check if BIC is a CIPS participant. 检查是否为 CIPS 参与行。 */
    public boolean isCipsParticipant(String bicCode) {
        return resolveByBic(bicCode) != null;
    }

    /** Get total registered participants. 获取已注册参与行数量。 */
    public int getParticipantCount() {
        return bicToCipsId.size();
    }
}
