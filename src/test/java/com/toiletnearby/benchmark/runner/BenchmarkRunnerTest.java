package com.toiletnearby.benchmark.runner;

import com.toiletnearby.benchmark.dto.NearbyBenchmarkSummaryDto;
import com.toiletnearby.benchmark.service.BenchmarkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class BenchmarkRunnerTest {

    @Test
    @DisplayName("BenchmarkRunner는 설정값으로 benchmark service를 실행한다")
    void runBenchmark() {
        BenchmarkService benchmarkService = mock(BenchmarkService.class);
        BenchmarkRunner runner = new BenchmarkRunner(benchmarkService);

        // @Value로 주입되는 값은 Spring 없이 단위 테스트하기 위해 직접 넣어준다.
        ReflectionTestUtils.setField(runner, "lng", 126.99885);
        ReflectionTestUtils.setField(runner, "lat", 37.59036);
        ReflectionTestUtils.setField(runner, "range", 3000);
        ReflectionTestUtils.setField(runner, "runs", 30);
        ReflectionTestUtils.setField(runner, "warmupRuns", 5);

        NearbyBenchmarkSummaryDto summary = new NearbyBenchmarkSummaryDto(
                Instant.now(),
                126.99885,
                37.59036,
                3000,
                30,
                5,
                List.of()
        );

        given(benchmarkService.compareNearbyToiletSearch(
                126.99885,
                37.59036,
                3000,
                30,
                5
        )).willReturn(summary);

        runner.run();

        // Runner가 직접 성능 측정을 하는 게 아니라 Service에 위임하는지 확인한다.
        then(benchmarkService).should().compareNearbyToiletSearch(
                126.99885,
                37.59036,
                3000,
                30,
                5
        );
    }
}
