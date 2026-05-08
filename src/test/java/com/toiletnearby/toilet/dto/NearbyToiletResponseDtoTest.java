package com.toiletnearby.toilet.dto;

import com.toiletnearby.toilet.domain.Toilet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// NearbyToiletResponseDto 변환 규칙을 테스트
class NearbyToiletResponseDtoTest {

    @Test
    @DisplayName("Toilet 엔티티와 거리 값을 응답 DTO로 변환한다")
    void fromToilet() {
        Toilet toilet = Toilet.create("서울역 화장실", 126.9723, 37.5559);
        toilet.updateDetails("서울특별시 중구 세종대로", "상시 개방");
        toilet.addRating(5);
        toilet.addRating(4);

        NearbyToiletResponseDto response = NearbyToiletResponseDto.from(toilet, 1234.56);

        assertThat(response.name()).isEqualTo("서울역 화장실");
        assertThat(response.address()).isEqualTo("서울특별시 중구 세종대로");
        assertThat(response.memo()).isEqualTo("상시 개방");
        assertThat(response.xWgs()).isEqualTo(126.9723);
        assertThat(response.yWgs()).isEqualTo(37.5559);
        assertThat(response.star()).isEqualTo(9);
        assertThat(response.player()).isEqualTo(2);
        assertThat(response.grade()).isEqualTo(4.5f);
        assertThat(response.distanceMeters()).isEqualTo(1235L);
    }
}
