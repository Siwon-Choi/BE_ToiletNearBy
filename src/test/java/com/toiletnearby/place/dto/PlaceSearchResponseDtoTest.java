package com.toiletnearby.place.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// 장소 검색 응답 DTO의 필드 보존을 테스트한다.
class PlaceSearchResponseDtoTest {

    @Test
    @DisplayName("장소 검색 응답을 만든다")
    void createPlaceSearchResponse() {
        // given & when: Kakao 검색 결과를 응답 DTO에 담는다.
        PlaceSearchResponseDto response = new PlaceSearchResponseDto(
                "1",
                "서울역",
                "교통,수송 > 기차역",
                "02-000-0000",
                "서울 중구",
                "서울 중구 한강대로",
                126.9723,
                37.5559,
                "https://place.map.kakao.com/1",
                120
        );

        // then: 응답에 필요한 값이 그대로 유지된다.
        assertThat(response.id()).isEqualTo("1");
        assertThat(response.name()).isEqualTo("서울역");
        assertThat(response.x()).isEqualTo(126.9723);
        assertThat(response.y()).isEqualTo(37.5559);
        assertThat(response.distance()).isEqualTo(120);
    }
}
