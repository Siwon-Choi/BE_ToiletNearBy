package com.toiletnearby.benchmark.dto;

// benchmark에서 한 검색 방식의 측정 결과
public record NearbyBenchmarkResultDto(
        String mode,
        String description,
        int candidateCount,
        int resultCount,
        double totalMs,
        double averageMs,
        double p95Ms,
        double throughputPerSecond
) {
}
