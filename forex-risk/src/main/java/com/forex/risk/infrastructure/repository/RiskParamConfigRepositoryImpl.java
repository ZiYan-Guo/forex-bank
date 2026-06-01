package com.forex.risk.infrastructure.repository;

import com.forex.risk.domain.model.entity.RiskParamConfig;
import com.forex.risk.domain.repository.RiskParamConfigRepository;
import com.forex.risk.infrastructure.mapper.RiskParamConfigMapper;
import com.forex.risk.infrastructure.persistence.RiskParamConfigPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RiskParamConfigRepositoryImpl implements RiskParamConfigRepository {

    private final RiskParamConfigMapper riskParamConfigMapper;

    @Override
    public List<RiskParamConfig> findAllEnabled() {
        List<RiskParamConfigPO> pos = riskParamConfigMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RiskParamConfigPO>()
                        .eq(RiskParamConfigPO::getIsEnabled, 1));
        return pos.stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<RiskParamConfig> findByParamKey(String key) {
        RiskParamConfigPO po = riskParamConfigMapper.findByParamKey(key);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<RiskParamConfig> findByParamType(String type) {
        return riskParamConfigMapper.findByParamType(type).stream()
                .map(this::toDomain)
                .toList();
    }

    private RiskParamConfig toDomain(RiskParamConfigPO po) {
        return new RiskParamConfig(
                po.getId(),
                po.getParamKey(),
                po.getParamValue(),
                po.getParamType(),
                po.getCurrency(),
                po.getIsEnabled());
    }

    private RiskParamConfigPO toPO(RiskParamConfig config) {
        RiskParamConfigPO po = new RiskParamConfigPO();
        po.setId(config.getId());
        po.setParamKey(config.getParamKey());
        po.setParamValue(config.getParamValue());
        po.setParamType(config.getParamType());
        po.setCurrency(config.getCurrency());
        po.setIsEnabled(config.getIsEnabled());
        return po;
    }
}
