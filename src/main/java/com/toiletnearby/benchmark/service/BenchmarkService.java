package com.toiletnearby.benchmark.service;

import com.toiletnearby.benchmark.dto.NearbyBenchmarkResultDto;
import com.toiletnearby.benchmark.dto.NearbyBenchmarkSummaryDto;
import com.toiletnearby.toilet.service.NearbySearchResult;
import com.toiletnearby.toilet.service.ToiletService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

// benchmark 프로필에서 근처 화장실 검색 성능을 비교한다.
@Profile("benchmark")
@Service
@RequiredArgsConstructor
public class BenchmarkService {

    private final ToiletService toiletService;

    // legacy, index, index-grid-cache 검색 방식을 같은 조건으로 비교한다.
    public NearbyBenchmarkSummaryDto compareNearbyToiletSearch(
            double lng,
            double lat,
            int range,
            int runs,
            int warmupRuns
    ) {
        int safeRange = normalizeRange(range);
        int safeRuns = clamp(runs, 1, 1000);
        int safeWarmupRuns = clamp(warmupRuns, 0, 100);

        List<NearbyBenchmarkResultDto> results = List.of(
                benchmark(
                        BenchmarkMode.LEGACY,
                        "findAll + distance filter",
                        safeRuns,
                        safeWarmupRuns,
                        () -> toiletService.searchToiletsNearByLegacy(safeRange, lng, lat)
                ),
                benchmark(
                        BenchmarkMode.INDEX,
                        "bounding box index + distance filter",
                        safeRuns,
                        safeWarmupRuns,
                        () -> toiletService.searchToiletsNearByIndexed(safeRange, lng, lat)
                ),
                benchmark(
                        BenchmarkMode.INDEX_GRID_CACHE,
                        "bounding box index + grid cache + distance filter",
                        safeRuns,
                        safeWarmupRuns,
                        () -> toiletService.searchToiletsNearByIndexedGridCached(safeRange, lng, lat)
                )
        );

        return new NearbyBenchmarkSummaryDto(
                Instant.now(),
                lng,
                lat,
                safeRange,
                safeRuns,
                safeWarmupRuns,
                results
        );
    }

    // 특정 검색 방식을 여러 번 실행하고 측정값을 만든다.
    private NearbyBenchmarkResultDto benchmark(
            BenchmarkMode mode,
            String description,
            int runs,
            int warmupRuns,
            Supplier<NearbySearchResult> search
    ) {
        for (int i = 0; i < warmupRuns; i++) {
            search.get();
        }

        List<Double> elapsedMs = new ArrayList<>();
        int candidateCount = 0;
        int resultCount = 0;

        long totalStartedAt = System.nanoTime();

        for (int i = 0; i < runs; i++) {
            long startedAt = System.nanoTime();

            NearbySearchResult result = search.get();

            long elapsedNanos = System.nanoTime() - startedAt;

            candidateCount = result.candidateCount();
            resultCount = result.toilets().size();
            elapsedMs.add(toMillis(elapsedNanos));
        }

        double totalMs = toMillis(System.nanoTime() - totalStartedAt);
        double averageMs = elapsedMs.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
        double throughputPerSecond = totalMs == 0 ? 0 : round(runs / (totalMs / 1000.0));

        return new NearbyBenchmarkResultDto(
                mode.name(),
                description,
                candidateCount,
                resultCount,
                round(totalMs),
                round(averageMs),
                percentile(elapsedMs, 95),
                throughputPerSecond
        );
    }

    // nano second를 millisecond로 바꾼다.
    private double toMillis(long nanos) {
        return round(nanos / 1000000.0);
    }

    // percentile 값을 계산한다.
    private double percentile(List<Double> values, int percentile) {
        if (values.isEmpty()) {
            return 0;
        }

        List<Double> sortedValues = new ArrayList<>(values);
        Collections.sort(sortedValues);

        int index = (int) Math.ceil((percentile / 100.0) * sortedValues.size()) - 1;
        int boundedIndex = Math.max(0, Math.min(index, sortedValues.size() - 1));

        return round(sortedValues.get(boundedIndex));
    }

    // 너무 큰 실행 횟수로 서버가 버벅이지 않게 제한한다.
    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    // 검색 반경은 1m 이상이어야 한다.
    private int normalizeRange(int range) {
        return Math.max(1, range);
    }

    // 소수점 3자리까지 반올림한다.
    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
