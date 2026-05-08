package com.toiletnearby.place.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 장소 검색 조건의 요청 검증을 테스트한다.
class PlaceSearchConditionTest {

    @Test
    @DisplayName("검색 조건을 만든다")
    void createCondition() {
        // given & when: 검색어 앞뒤 공백은 제거하고 조건을 만든다.
        PlaceSearchCondition condition = PlaceSearchCondition.of(
                " 서울역 ",
                1,
                10,
                126.9723,
                37.5559,
                1000
        );

        // then: Kakao API에 전달 가능한 검색 조건이 만들어진다.
        assertThat(condition.query()).isEqualTo("서울역");
        assertThat(condition.hasCoordinate()).isTrue();
        assertThat(condition.radius()).isEqualTo(1000);
    }

    @Test
    @DisplayName("검색어는 필수다")
    void queryIsRequired() {
        // 검색어가 없으면 Kakao API 호출 자체가 의미 없으므로 400 예외를 낸다.
        assertThatThrownBy(() -> PlaceSearchCondition.of(" ", 1, 10, null, null, null))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("x와 y는 함께 전달해야 한다")
    void xAndYMustBeTogether() {
        // 위치 기반 검색은 경도와 위도가 한 쌍이어야 한다.
        assertThatThrownBy(() -> PlaceSearchCondition.of("서울역", 1, 10, 126.9723, null, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("x와 y는 함께 전달해야 합니다.");
    }

    @Test
    @DisplayName("radius를 사용하려면 x와 y가 필요하다")
    void radiusNeedsCoordinate() {
        // radius는 중심 좌표 기준 반경이므로 좌표 없이 보낼 수 없다.
        assertThatThrownBy(() -> PlaceSearchCondition.of("서울역", 1, 10, null, null, 1000))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("radius를 사용하려면 x와 y가 필요합니다.");
    }
}
