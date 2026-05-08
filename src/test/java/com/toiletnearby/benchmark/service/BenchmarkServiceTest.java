package com.toiletnearby.benchmark.service;

import com.toiletnearby.benchmark.dto.NearbyBenchmarkSummaryDto;
import com.toiletnearby.toilet.dto.NearbyToiletResponseDto;
import com.toiletnearby.toilet.service.NearbySearchResult;
import com.toiletnearby.toilet.service.ToiletService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class BenchmarkServiceTest {

    @Test
    @DisplayName("근처 화장실 benchmark는 세 가지 검색 방식을 모두 비교한다")
    void compareNearbyToiletSearch() {
        ToiletService toiletService = mock(ToiletService.class);
        BenchmarkService benchmarkService = new BenchmarkService(toiletService);

        double lng = 126.99885;
        double lat = 37.59036;
        int range = 3000;

        // 실제 DB를 붙이지 않고, 각 검색 방식이 반환했다고 가정할 결과를 만든다.
        given(toiletService.searchToiletsNearByLegacy(range, lng, lat))
                .willReturn(createSearchResult(29135, 2));
        given(toiletService.searchToiletsNearByIndexed(range, lng, lat))
                .willReturn(createSearchResult(335, 2));
        given(toiletService.searchToiletsNearByIndexedGridCached(range, lng, lat))
                .willReturn(createSearchResult(480, 2));

        NearbyBenchmarkSummaryDto summary = benchmarkService.compareNearbyToiletSearch(
                lng,
                lat,
                range,
                1,
                0
        );

        // benchmark 결과는 세 가지 방식이 모두 포함되어야 한다.
        assertThat(summary.results())
                .extracting("mode")
                .containsExactly("LEGACY", "INDEX", "INDEX_GRID_CACHE");

        // 각 방식별 후보 개수가 결과에 반영되는지 확인한다.
        assertThat(summary.results())
                .extracting("candidateCount")
                .containsExactly(29135, 335, 480);

        // 실제 검색 메서드가 각각 한 번씩 호출됐는지 확인한다.
        then(toiletService).should().searchToiletsNearByLegacy(range, lng, lat);
        then(toiletService).should().searchToiletsNearByIndexed(range, lng, lat);
        then(toiletService).should().searchToiletsNearByIndexedGridCached(range, lng, lat);
    }

    @Test
    @DisplayName("benchmark 입력값은 안전한 범위로 보정된다")
    void normalizeBenchmarkParameters() {
        ToiletService toiletService = mock(ToiletService.class);
        BenchmarkService benchmarkService = new BenchmarkService(toiletService);

        double lng = 126.99885;
        double lat = 37.59036;

        given(toiletService.searchToiletsNearByLegacy(1, lng, lat))
                .willReturn(createSearchResult(0, 0));
        given(toiletService.searchToiletsNearByIndexed(1, lng, lat))
                .willReturn(createSearchResult(0, 0));
        given(toiletService.searchToiletsNearByIndexedGridCached(1, lng, lat))
                .willReturn(createSearchResult(0, 0));

        NearbyBenchmarkSummaryDto summary = benchmarkService.compareNearbyToiletSearch(
                lng,
                lat,
                0,
                0,
                -1
        );

        // range는 최소 1m, runs는 최소 1회, warmupRuns는 최소 0회로 보정된다.
        assertThat(summary.range()).isEqualTo(1);
        assertThat(summary.runs()).isEqualTo(1);
        assertThat(summary.warmupRuns()).isEqualTo(0);
    }

    private NearbySearchResult createSearchResult(int candidateCount, int resultCount) {
        List<NearbyToiletResponseDto> toilets = new ArrayList<>();

        for (int i = 0; i < resultCount; i++) {
            toilets.add(new NearbyToiletResponseDto(
                    (long) i + 1,
                    "화장실" + i,
                    "메모",
                    "주소",
                    126.99885,
                    37.59036,
                    0,
                    0,
                    0,
                    10
            ));
        }

        return new NearbySearchResult(toilets, candidateCount);
    }
}
