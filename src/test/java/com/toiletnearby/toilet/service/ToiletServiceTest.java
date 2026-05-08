package com.toiletnearby.toilet.service;

import com.toiletnearby.toilet.domain.Toilet;
import com.toiletnearby.toilet.dto.NearbyToiletResponseDto;
import com.toiletnearby.toilet.repository.ToiletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ToiletServiceTest {

    @Mock
    private ToiletRepository toiletRepository;

    @InjectMocks
    private ToiletService toiletService;

    @Mock
    private ToiletLocationCandidateService toiletLocationCandidateService;


    @Test
    @DisplayName("모든 화장실을 조회한다")
    void getToilets() {
        List<Toilet> toilets = List.of(
                Toilet.create("서울역 화장실", 126.9723, 37.5559),
                Toilet.create("시청역 화장실", 126.9784, 37.5665)
        );

        given(toiletRepository.findAll()).willReturn(toilets);

        List<Toilet> result = toiletService.getToilets();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("id로 화장실 하나를 조회한다")
    void getToilet() {
        Toilet toilet = Toilet.create("서울역 화장실", 126.9723, 37.5559);

        given(toiletRepository.findById(1L)).willReturn(Optional.of(toilet));

        Toilet result = toiletService.getToilet(1L);

        assertThat(result.getName()).isEqualTo("서울역 화장실");
    }

    @Test
    @DisplayName("존재하지 않는 화장실을 조회하면 예외가 발생한다")
    void getUnknownToilet() {
        given(toiletRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> toiletService.getToilet(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("화장실을 찾을 수 없습니다.");
    }


    @Test
    @DisplayName("사용자 좌표 기준 반경 안의 화장실만 조회한다")
    void getToiletsNearBy() {
        Toilet seoulStation = Toilet.create("서울역 화장실", 126.9723, 37.5559);
        Toilet busanStation = Toilet.create("부산역 화장실", 129.0396, 35.1151);

        double gridLatitude = ToiletSearchGrid.snap(37.5559);
        double gridLongitude = ToiletSearchGrid.snap(126.9723);

        given(toiletLocationCandidateService.findCandidates(gridLatitude, gridLongitude, 2000))
                .willReturn(List.of(seoulStation, busanStation));

        List<NearbyToiletResponseDto> result = toiletService.getToiletsNearBy(
                2000,
                126.9723,
                37.5559
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("서울역 화장실");

        then(toiletRepository).should(never()).findAll();
    }


    @Test
    @DisplayName("legacy 검색은 전체 조회 후 거리 계산을 수행한다")
    void searchToiletsNearByLegacy() {
        Toilet seoulStation = Toilet.create("서울역 화장실", 126.9723, 37.5559);
        Toilet busanStation = Toilet.create("부산역 화장실", 129.0396, 35.1151);

        given(toiletRepository.findAll()).willReturn(List.of(seoulStation, busanStation));

        NearbySearchResult result = toiletService.searchToiletsNearByLegacy(
                2000,
                126.9723,
                37.5559
        );

        assertThat(result.candidateCount()).isEqualTo(2);
        assertThat(result.toilets()).hasSize(1);
    }

    @Test
    @DisplayName("indexed 검색은 bounding box 후보만 거리 계산한다")
    void searchToiletsNearByIndexed() {
        Toilet seoulStation = Toilet.create("서울역 화장실", 126.9723, 37.5559);
        Toilet busanStation = Toilet.create("부산역 화장실", 129.0396, 35.1151);

        given(toiletLocationCandidateService.findIndexedCandidates(37.5559, 126.9723, 2000))
                .willReturn(List.of(seoulStation, busanStation));

        NearbySearchResult result = toiletService.searchToiletsNearByIndexed(
                2000,
                126.9723,
                37.5559
        );

        assertThat(result.candidateCount()).isEqualTo(2);
        assertThat(result.toilets()).hasSize(1);

        then(toiletRepository).should(never()).findAll();
    }

    @Test
    @DisplayName("grid cache 검색은 grid 좌표로 후보를 조회한다")
    void searchToiletsNearByIndexedGridCached() {
        Toilet seoulStation = Toilet.create("서울역 화장실", 126.9723, 37.5559);
        Toilet busanStation = Toilet.create("부산역 화장실", 129.0396, 35.1151);

        double gridLatitude = ToiletSearchGrid.snap(37.5559);
        double gridLongitude = ToiletSearchGrid.snap(126.9723);

        given(toiletLocationCandidateService.findCandidates(gridLatitude, gridLongitude, 2000))
                .willReturn(List.of(seoulStation, busanStation));

        NearbySearchResult result = toiletService.searchToiletsNearByIndexedGridCached(
                2000,
                126.9723,
                37.5559
        );

        assertThat(result.candidateCount()).isEqualTo(2);
        assertThat(result.toilets()).hasSize(1);

        then(toiletRepository).should(never()).findAll();
    }


    @Test
    @DisplayName("메모 평점을 화장실 평점 통계에 반영한다")
    void applyMemoRating() {
        Toilet toilet = Toilet.create("서울역 화장실", 126.9723, 37.5559);

        given(toiletRepository.findById(1L)).willReturn(Optional.of(toilet));

        toiletService.applyMemoRating(1L, 5);

        assertThat(toilet.getStar()).isEqualTo(5);
        assertThat(toilet.getPlayer()).isEqualTo(1);
        assertThat(toilet.getGrade()).isEqualTo(5.0f);
    }

    @Test
    @DisplayName("검색 반경은 1m 이상이어야 한다")
    void rangeMetersIsRequired() {
        assertThatThrownBy(() -> toiletService.getToiletsNearBy(0, 126.9723, 37.5559))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("rangeMeters는 1 이상이어야 합니다.");
    }
}
