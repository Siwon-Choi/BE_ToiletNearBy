package com.toiletnearby.benchmark.runner;

import com.toiletnearby.benchmark.dto.NearbyBenchmarkResultDto;
import com.toiletnearby.benchmark.dto.NearbyBenchmarkSummaryDto;
import com.toiletnearby.benchmark.service.BenchmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// benchmark 프로필이 켜졌을 때 콘솔에 검색 성능 비교 결과를 출력한다.
@Slf4j
@Profile("benchmark")
@Component
@RequiredArgsConstructor
public class BenchmarkRunner implements CommandLineRunner {

    private final BenchmarkService benchmarkService;

    @Value("${benchmark.nearby.lng:126.99885}")
    private double lng;

    @Value("${benchmark.nearby.lat:37.59036}")
    private double lat;

    @Value("${benchmark.nearby.range:3000}")
    private int range;

    @Value("${benchmark.nearby.runs:30}")
    private int runs;

    @Value("${benchmark.nearby.warmup-runs:5}")
    private int warmupRuns;

    // 애플리케이션 시작 후 benchmark를 실행하고 로그로 결과를 남긴다.
    @Override
    public void run(String... args) {
        NearbyBenchmarkSummaryDto summary = benchmarkService.compareNearbyToiletSearch(
                lng,
                lat,
                range,
                runs,
                warmupRuns
        );

        log.info(
                "[Benchmark] nearby toilet search measuredAt={}, lng={}, lat={}, range={}, runs={}, warmupRuns={}",
                summary.measuredAt(),
                summary.lng(),
                summary.lat(),
                summary.range(),
                summary.runs(),
                summary.warmupRuns()
        );

        for (NearbyBenchmarkResultDto result : summary.results()) {
            log.info(
                    "[Benchmark] {} | {} | total={}ms avg={}ms p95={}ms throughput={}req/s candidates={} results={}",
                    result.mode(),
                    result.description(),
                    result.totalMs(),
                    result.averageMs(),
                    result.p95Ms(),
                    result.throughputPerSecond(),
                    result.candidateCount(),
                    result.resultCount()
            );
        }
    }
}
