package com.toiletnearby.toilet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "toilets",
        indexes = {
                @Index(name = "idx_toilets_location", columnList = "x_wgs,y_wgs")
        }
)

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Toilet {

    private static final double EARTH_RADIUS_METERS = 6371000.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 화장실 이름
    @Column(nullable = false, length = 100)
    private String name;

    // 개방 시간 같은 추가 설명
    @Column(length = 500)
    private String memo;

    // 주소
    @Column(length = 255)
    private String address;

    // 경도, 원본의 x_wgs에 해당한다.
    @Column(name = "x_wgs", nullable = false)
    private double xWgs;

    // 위도, 원본의 y_wgs에 해당한다.
    @Column(name = "y_wgs", nullable = false)
    private double yWgs;

    // star/player를 하나의 평점 통계 값 객체로 묶는다.
    @Embedded
    private ToiletRatingStats ratingStats;

    private Toilet(String name, double xWgs, double yWgs, ToiletRatingStats ratingStats) {
        this.name = name;
        this.memo = null;
        this.address = null;
        this.xWgs = xWgs;
        this.yWgs = yWgs;
        this.ratingStats = ratingStats;
    }

    // 기본 화장실 정보를 생성한다.
    public static Toilet create(String name, double xWgs, double yWgs) {
        return create(name, xWgs, yWgs, 0, 0);
    }

    // 평점 초기값까지 포함해서 화장실 정보를 생성한다.
    public static Toilet create(String name, double xWgs, double yWgs, int star, int player) {
        validateRequiredName(name);
        validateLongitude(xWgs);
        validateLatitude(yWgs);
        ToiletRatingStats ratingStats = ToiletRatingStats.of(star, player);

        return new Toilet(name, xWgs, yWgs, ratingStats);
    }

    // CSV import 때 주소와 메모 정보를 나중에 채우기 위한 메서드다.
    public void updateDetails(String address, String memo) {
        this.address = address;
        this.memo = memo;
    }

    // 메모 평점이 추가될 때 누적 점수와 평가 수를 갱신한다.
    public void addRating(int good) {
        this.ratingStats = ratingStats.addRating(good);
    }

    // 평균 평점은 DB에 저장하지 않고 계산해서 반환한다.
    // grade는 star와 player로 계산 가능한 파생값이기 때문이다.
    public float getGrade() {
        return ratingStats.getGrade();
    }

    // Haversine 공식
    // 두 WGS84 좌표 사이의 거리를 meter 단위로 계산한다.
    public static double distance(double xWgs1, double yWgs1, double xWgs2, double yWgs2) {

        // 라디안으로 변환
        double latitude1 = Math.toRadians(yWgs1);
        double latitude2 = Math.toRadians(yWgs2);
        double latitudeDifference = Math.toRadians(yWgs2 - yWgs1);
        double longitudeDifference = Math.toRadians(xWgs2 - xWgs1);

        // Haversine 값
        double haversine = Math.sin(latitudeDifference / 2) * Math.sin(latitudeDifference / 2)
                + Math.cos(latitude1) * Math.cos(latitude2)
                * Math.sin(longitudeDifference / 2) * Math.sin(longitudeDifference / 2);

        // 중심각
        double centralAngle = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));

        // 거리 = 지구 반지름 * 중심각
        return EARTH_RADIUS_METERS * centralAngle;
    }

    private static void validateRequiredName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 필수입니다.");
        }
    }

    private static void validateLongitude(double xWgs) {
        if (Double.isNaN(xWgs) || xWgs < -180 || xWgs > 180) {
            throw new IllegalArgumentException("xWgs는 -180 이상 180 이하여야 합니다.");
        }
    }

    private static void validateLatitude(double yWgs) {
        if (Double.isNaN(yWgs) || yWgs < -90 || yWgs > 90) {
            throw new IllegalArgumentException("yWgs는 -90 이상 90 이하여야 합니다.");
        }
    }

    public int getStar() {
        return ratingStats.getStar();
    }

    public int getPlayer() {
        return ratingStats.getPlayer();
    }
}
