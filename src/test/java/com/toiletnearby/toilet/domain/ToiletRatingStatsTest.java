package com.toiletnearby.toilet.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// ToiletRatingStats 값 객체의 규칙을 테스트한다.
class ToiletRatingStatsTest {

    @Test
    @DisplayName("초기 평점 통계는 0으로 시작한다")
    void empty() {
        ToiletRatingStats stats = ToiletRatingStats.empty();

        assertThat(stats.getStar()).isZero();
        assertThat(stats.getPlayer()).isZero();
        assertThat(stats.getGrade()).isEqualTo(0.0f);
    }

    @Test
    @DisplayName("기존 평점 통계로 생성할 수 있다")
    void createWithStats() {
        ToiletRatingStats stats = ToiletRatingStats.of(9, 2);

        assertThat(stats.getStar()).isEqualTo(9);
        assertThat(stats.getPlayer()).isEqualTo(2);
        assertThat(stats.getGrade()).isEqualTo(4.5f);
    }

    @Test
    @DisplayName("평점을 추가하면 새 통계 객체를 반환한다")
    void addRating() {
        ToiletRatingStats stats = ToiletRatingStats.empty()
                .addRating(5)
                .addRating(4);

        assertThat(stats.getStar()).isEqualTo(9);
        assertThat(stats.getPlayer()).isEqualTo(2);
        assertThat(stats.getGrade()).isEqualTo(4.5f);
    }

    @Test
    @DisplayName("평점은 1 이상 5 이하여야 한다")
    void ratingRange() {
        ToiletRatingStats stats = ToiletRatingStats.empty();

        assertThatThrownBy(() -> stats.addRating(6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("good은 1 이상 5 이하여야 합니다.");
    }

    @Test
    @DisplayName("누적 점수와 평가 수는 0 이상이어야 한다")
    void statsRange() {
        assertThatThrownBy(() -> ToiletRatingStats.of(-1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("star는 0 이상이어야 합니다.");

        assertThatThrownBy(() -> ToiletRatingStats.of(0, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("player는 0 이상이어야 합니다.");
    }
}
