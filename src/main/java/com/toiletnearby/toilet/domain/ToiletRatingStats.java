package com.toiletnearby.toilet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 화장실 평점 통계를 표현하는 값 객체다.
// Java에서는 하나의 객체로 다루지만 DB에는 star, player 컬럼으로 저장된다.
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToiletRatingStats {

    // 누적 평점 합계
    @Column(nullable = false)
    private int star;

    // 평점을 남긴 사람 수
    @Column(nullable = false)
    private int player;

    private ToiletRatingStats(int star, int player) {
        validateReviewStats(star, player);

        this.star = star;
        this.player = player;
    }

    // 아직 평점이 없는 초기 상태를 만든다.
    public static ToiletRatingStats empty() {
        return new ToiletRatingStats(0, 0);
    }

    // CSV import처럼 기존 평점 통계를 가진 상태를 만든다.
    public static ToiletRatingStats of(int star, int player) {
        return new ToiletRatingStats(star, player);
    }

    // 새 평점을 반영한 통계 객체를 만든다.
    public ToiletRatingStats addRating(int good) {
        validateRating(good);

        return new ToiletRatingStats(star + good, player + 1);
    }

    // 평균 평점은 저장하지 않고 누적 합계와 평가 수로 계산한다.
    public float getGrade() {
        if (player == 0) {
            return 0.0f;
        }

        return (float) star / player;
    }

    private static void validateReviewStats(int star, int player) {
        if (star < 0) {
            throw new IllegalArgumentException("star는 0 이상이어야 합니다.");
        }

        if (player < 0) {
            throw new IllegalArgumentException("player는 0 이상이어야 합니다.");
        }
    }

    private static void validateRating(int good) {
        if (good < 1 || good > 5) {
            throw new IllegalArgumentException("good은 1 이상 5 이하여야 합니다.");
        }
    }
}
