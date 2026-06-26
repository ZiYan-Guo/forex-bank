package com.forex.cashpool.domain.service;

import com.forex.cashpool.domain.model.entity.PoolMember;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

/**
 * 轧差结算服务 - 计算资金池成员间的净头寸并生成轧差指令
 * Netting Settlement Service - Calculate net positions between pool members and generate netting instructions
 */
@Slf4j
@Service
@Transactional
public class NettingSettlementService {

    /**
     * 计算净头寸 - 基于成员列表与结算日期统计各成员的应收/应付净值
     * Calculate net positions - Aggregate each member's net payable/receivable based on member list and settlement date
     * 返回 Map<成员账户ID, 净头寸>，正数为应收，负数为应付
     * Returns Map<memberAccountId, netPosition>, positive = receivable, negative = payable
     */
    public Map<Long, BigDecimal> calculateNetPosition(List<PoolMember> members, LocalDate date) {
        log.info("开始计算轧差净头寸, 结算日期: {}, 成员数量: {}", date, members != null ? members.size() : 0);
        Map<Long, BigDecimal> netPositions = new HashMap<>();

        if (members == null || members.isEmpty()) {
            log.warn("资金池无成员，净头寸为空");
            return netPositions;
        }

        for (PoolMember member : members) {
            BigDecimal contribution = member.getContributionLimit();
            netPositions.put(member.getMemberAccountId(), contribution);
        }

        BigDecimal totalNet = netPositions.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("轧差净头寸计算完成, 结算日期: {}, 涉及成员数: {}, 净头寸合计: {}",
                date, netPositions.size(), totalNet);

        return netPositions;
    }

    /**
     * 生成轧差结算指令 - 根据净头寸生成收付款结算指令
     * Generate netting settlement instructions based on net positions
     * 正数净头寸方为收款方，负数净头寸方为付款方
     * Positive net position = receiving party, negative = paying party
     */
    public String generateNettingInstruction(Map<Long, BigDecimal> netPositions, String poolId, LocalDate date) {
        log.info("生成轧差结算指令, poolId: {}, 结算日期: {}, 头寸数量: {}",
                poolId, date, netPositions.size());

        StringBuilder instruction = new StringBuilder();
        instruction.append("===== 轧差结算指令 =====\n");
        instruction.append("资金池编号: ").append(poolId).append("\n");
        instruction.append("结算日期: ").append(date).append("\n");
        instruction.append("------------------------------\n");

        for (Map.Entry<Long, BigDecimal> entry : netPositions.entrySet()) {
            Long accountId = entry.getKey();
            BigDecimal netPosition = entry.getValue();
            String direction = netPosition.compareTo(BigDecimal.ZERO) >= 0 ? "应收(Receive)" : "应付(Pay)";
            instruction.append(String.format("账户[%d] -> %s: %s\n",
                    accountId, direction, netPosition.abs()));
        }

        instruction.append("==============================");
        log.info("轧差结算指令已生成:\n{}", instruction);
        return instruction.toString();
    }
}
