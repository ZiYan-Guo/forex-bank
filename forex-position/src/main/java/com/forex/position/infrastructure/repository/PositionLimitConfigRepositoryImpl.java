package com.forex.position.infrastructure.repository;

import com.forex.position.domain.model.entity.PositionLimitConfig;
import com.forex.position.domain.repository.PositionLimitConfigRepository;
import com.forex.position.infrastructure.mapper.PositionLimitConfigMapper;
import com.forex.position.infrastructure.persistence.PositionLimitConfigPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PositionLimitConfigRepositoryImpl implements PositionLimitConfigRepository {

    private final PositionLimitConfigMapper mapper;

    @Override
    public PositionLimitConfig save(PositionLimitConfig config) {
        PositionLimitConfigPO po = toPO(config);
        if (config.getId() == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<PositionLimitConfig> findById(Long id) {
        PositionLimitConfigPO po = mapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<PositionLimitConfig> findByCurrencyAndType(String currency, String limitType) {
        List<PositionLimitConfigPO> poList = mapper.selectByCurrencyAndType(currency, limitType);
        return poList.stream().map(this::toDomain).toList();
    }

    @Override
    public List<PositionLimitConfig> findAllEnabled() {
        List<PositionLimitConfigPO> poList = mapper.selectAllEnabled();
        return poList.stream().map(this::toDomain).toList();
    }

    @Override
    public List<PositionLimitConfig> findByCurrency(String currency) {
        List<PositionLimitConfigPO> poList = mapper.selectByCurrency(currency);
        return poList.stream().map(this::toDomain).toList();
    }

    private PositionLimitConfig toDomain(PositionLimitConfigPO po) {
        return new PositionLimitConfig(
                po.getId(),
                po.getCurrency(),
                po.getLimitType(),
                po.getLimitAmount(),
                po.getWarningPct(),
                po.getIsEnabled()
        );
    }

    private PositionLimitConfigPO toPO(PositionLimitConfig domain) {
        PositionLimitConfigPO po = new PositionLimitConfigPO();
        po.setId(domain.getId());
        po.setCurrency(domain.getCurrency());
        po.setLimitType(domain.getLimitType());
        po.setLimitAmount(domain.getLimitAmount());
        po.setWarningPct(domain.getWarningPct());
        po.setIsEnabled(domain.getIsEnabled());
        return po;
    }
}
