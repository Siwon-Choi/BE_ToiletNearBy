package com.toiletnearby.toilet.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Toilet 객체가 가져야 할 기본 규칙을 테스트한다.
class ToiletTest {

    @Test
    @DisplayName("화장실을 생성하면 기본 평점 정보는 0으로 시작한다")
    void createToilet() {
        Toilet toilet = Toilet.create("서울역 화장실", 126.9723, 37.5559);

        assertThat(toilet.getName()).isEqualTo("서울역 화장실");
        assertThat(toilet.getXWgs()).isEqualTo(126.9723);
        assertThat(toilet.getYWgs()).isEqualTo(37.5559);
        assertThat(toilet.getStar()).isZero();
        assertThat(toilet.getPlayer()).isZero();
        assertThat(toilet.getGrade()).isEqualTo(0.0f);
    }

    @Test
    @DisplayName("주소와 메모 정보를 수정한다")
    void updateDetails() {
        Toilet toilet = Toilet.create("서울역 화장실", 126.9723, 37.5559);

        toilet.updateDetails("서울특별시 중구 세종대로", "상시 개방");

        assertThat(toilet.getAddress()).isEqualTo("서울특별시 중구 세종대로");
        assertThat(toilet.getMemo()).isEqualTo("상시 개방");
    }

    @Test
    @DisplayName("평점을 추가하면 누적 점수와 평가 수가 증가한다")
    void addRating() {
        Toilet toilet = Toilet.create("서울역 화장실", 126.9723, 37.5559);

        toilet.addRating(5);
        toilet.addRating(4);

        assertThat(toilet.getStar()).isEqualTo(9);
        assertThat(toilet.getPlayer()).isEqualTo(2);
        assertThat(toilet.getGrade()).isEqualTo(4.5f);
    }

    @Test
    @DisplayName("평점은 1 이상 5 이하여야 한다")
    void ratingRange() {
        Toilet toilet = Toilet.create("서울역 화장실", 126.9723, 37.5559);

        assertThatThrownBy(() -> toilet.addRating(6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("good은 1 이상 5 이하여야 합니다.");
    }

    @Test
    @DisplayName("같은 좌표의 거리는 0m다")
    void distanceOfSameCoordinate() {
        double distance = Toilet.distance(126.9723, 37.5559, 126.9723, 37.5559);

        assertThat(distance).isEqualTo(0.0);
    }

    @Test
    @DisplayName("서울역과 시청역 사이의 거리를 meter 단위로 계산한다")
    void calculateDistance() {
        double distance = Toilet.distance(126.9723, 37.5559, 126.9784, 37.5665);

        assertThat(distance).isBetween(1000.0, 2000.0);
    }

    @Test
    @DisplayName("화장실 이름은 비어 있을 수 없다")
    void nameIsRequired() {
        assertThatThrownBy(() -> Toilet.create(" ", 126.9723, 37.5559))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name은 필수입니다.");
    }

    @Test
    @DisplayName("경도는 -180 이상 180 이하여야 한다")
    void longitudeRange() {
        assertThatThrownBy(() -> Toilet.create("서울역 화장실", 200.0, 37.5559))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("xWgs는 -180 이상 180 이하여야 합니다.");
    }

    @Test
    @DisplayName("위도는 -90 이상 90 이하여야 한다")
    void latitudeRange() {
        assertThatThrownBy(() -> Toilet.create("서울역 화장실", 126.9723, 100.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("yWgs는 -90 이상 90 이하여야 합니다.");
    }
}
