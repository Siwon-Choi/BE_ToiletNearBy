package com.toiletnearby.place.service;

import com.toiletnearby.place.client.PlaceSearchClient;
import com.toiletnearby.place.dto.PlaceSearchResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

// 장소 검색 Service의 위임 흐름을 테스트한다.
@ExtendWith(MockitoExtension.class)
class PlaceSearchServiceTest {

    @Mock
    private PlaceSearchClient placeSearchClient;

    @InjectMocks
    private PlaceSearchService placeSearchService;

    @Test
    @DisplayName("검색 조건을 검증한 뒤 장소 검색 client에 위임한다")
    void searchPlaces() {
        // given: 외부 장소 검색 client가 반환할 장소 검색 결과를 준비한다.
        PlaceSearchResponseDto response = new PlaceSearchResponseDto(
                "1",
                "서울역",
                "교통,수송 > 기차역",
                "",
                "서울 중구",
                "서울 중구 한강대로",
                126.9723,
                37.5559,
                "https://place.map.kakao.com/1",
                0
        );

        given(placeSearchClient.search(argThat(condition ->
                condition.query().equals("서울역")
                        && condition.page() == 1
                        && condition.size() == 10
                        && condition.x().equals(126.9723)
                        && condition.y().equals(37.5559)
                        && condition.radius().equals(1000)
        ))).willReturn(List.of(response));

        // when: 장소 검색을 요청한다.
        List<PlaceSearchResponseDto> result = placeSearchService.search(
                "서울역",
                1,
                10,
                126.9723,
                37.5559,
                1000
        );

        // then: 검증된 검색 조건으로 장소 검색 client가 호출된다.
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("서울역");
        then(placeSearchClient).should().search(argThat(condition ->
                condition.query().equals("서울역")
                        && condition.hasCoordinate()
                        && condition.radius().equals(1000)
        ));
    }
}
