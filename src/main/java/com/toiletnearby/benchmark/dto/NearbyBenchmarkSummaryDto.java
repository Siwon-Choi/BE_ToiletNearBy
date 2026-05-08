package com.toiletnearby.benchmark.dto;

import java.time.Instant;
import java.util.List;

// 근처 화장실 검색 benchmark 전체 요약 응답이다.
public record NearbyBenchmarkSummaryDto(
        Instant measuredAt,
        double lng,
        double lat,
        int range,
        int runs,
        int warmupRuns,
        List<NearbyBenchmarkResultDto> results
) {
}
