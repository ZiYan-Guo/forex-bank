package com.forex.position.application.service;

import com.forex.position.domain.model.aggregate.Position;
import com.forex.position.domain.repository.PositionRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Multi-dimensional exposure analysis service.
 * Provides exposure breakdown by currency, product type, trader, maturity bucket.
 * 多维度敞口分析服务。按币种/产品/交易员/到期日段分解敞口。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExposureAnalysisService {

    private final PositionRepository positionRepository;

    public ExposureAnalysisResult analyze(LocalDate date, String[] dimensions, String[] currencies) {
        List<Position> positions = loadPositions(date, currencies);
        ExposureAnalysisResult result = new ExposureAnalysisResult();
        result.setDate(date);
        result.setTotalPositions(positions.size());

        for (String dim : dimensions) {
            Map<String, ExposureDimension> breakdown = analyzeByDimension(positions, dim);
            result.getDimensions().put(dim, breakdown);
        }
        return result;
    }

    public MaturityLadder analyzeMaturityLadder(LocalDate date) {
        List<String> buckets = List.of("1M", "1-3M", "3-12M", "1Y+");
        List<Position> positions = loadPositions(date, null);
        MaturityLadder ladder = new MaturityLadder();
        ladder.setDate(date);
        for (Position p : positions) {
            String bucket = classifyBucket(p);
            ladder.addPosition(p.getPositionCurrency(), bucket, p.getLongAmount(), p.getShortAmount());
        }
        return ladder;
    }

    public HeatmapData generateHeatmap(LocalDate date) {
        List<Position> positions = loadPositions(date, null);
        HeatmapData data = new HeatmapData();
        Set<String> currencies = new TreeSet<>();
        Set<String> types = new TreeSet<>(List.of("SPOT", "FORWARD", "SWAP", "OPTION", "STRUCTURED"));

        for (Position p : positions) {
            currencies.add(p.getPositionCurrency());
            data.addValue(p.getPositionCurrency(), p.getPositionType(), p.getNetPosition().abs());
        }
        data.setXAxis(new ArrayList<>(types));
        data.setYAxis(new ArrayList<>(currencies));
        return data;
    }

    private Map<String, ExposureDimension> analyzeByDimension(List<Position> positions, String dimension) {
        Map<String, List<Position>> grouped = positions.stream()
                .collect(Collectors.groupingBy(p -> getDimensionKey(p, dimension)));
        Map<String, ExposureDimension> result = new LinkedHashMap<>();
        grouped.forEach((key, group) -> {
            ExposureDimension dim = new ExposureDimension();
            dim.setKey(key);
            dim.setPositionCount(group.size());
            dim.setTotalLong(group.stream().map(Position::getLongAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dim.setTotalShort(group.stream().map(Position::getShortAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dim.setNetPosition(dim.getTotalLong().subtract(dim.getTotalShort()));
            result.put(key, dim);
        });
        return result;
    }

    private String getDimensionKey(Position p, String dimension) {
        return switch (dimension) {
            case "currency" -> p.getPositionCurrency();
            case "productType" -> p.getPositionType();
            case "traderId" -> String.valueOf(p.getTraderId() != null ? p.getTraderId() : "0");
            case "branchCode" -> p.getBranchCode() != null ? p.getBranchCode() : "MAIN";
            default -> "UNKNOWN";
        };
    }

    private String classifyBucket(Position p) {
        LocalDate today = LocalDate.now();
        long daysToMaturity = today.until(p.getPositionDate()).getDays();
        if (daysToMaturity <= 30) {
            return "1M";
        }
        if (daysToMaturity <= 90) {
            return "1-3M";
        }
        if (daysToMaturity <= 365) {
            return "3-12M";
        }
        return "1Y+";
    }

    private List<Position> loadPositions(LocalDate date, String[] currencies) {
        return positionRepository.findByCurrencyPairAndDate(null, date);
    }

    @Data
    public static class ExposureAnalysisResult {
        private LocalDate date;
        private int totalPositions;
        private Map<String, Map<String, ExposureDimension>> dimensions = new HashMap<>();
    }

    @Data
    public static class ExposureDimension {
        private String key;
        private int positionCount;
        private BigDecimal totalLong;
        private BigDecimal totalShort;
        private BigDecimal netPosition;
    }

    @Data
    public static class MaturityLadder {
        private LocalDate date;
        private Map<String, Map<String, BigDecimal[]>> data = new HashMap<>();

        public void addPosition(String ccy, String bucket, BigDecimal longAmt, BigDecimal shortAmt) {
            data.computeIfAbsent(ccy, k -> new HashMap<>())
                    .put(bucket, new BigDecimal[]{longAmt, shortAmt});
        }
    }

    @Data
    public static class HeatmapData {
        private List<String> xAxis;
        private List<String> yAxis;
        private List<double[]> data = new ArrayList<>();

        public void addValue(String ccy, String type, BigDecimal value) {
            double[] arr = new double[]{0.0};
            arr[0] = value.doubleValue();
            data.add(arr);
        }
    }
}
