package com.toiletnearby.toilet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toiletnearby.toilet.domain.Toilet;

// 근처 화장실 검색 응답 DTO
public record NearbyToiletResponseDto(
        Long id,
        String name,
        String memo,
        String address,
        @JsonProperty("x_wgs") double xWgs,
        @JsonProperty("y_wgs") double yWgs,
        int star,
        int player,
        float grade,
        long distanceMeters
) {

    // Toilet 엔티티와 계산된 거리를 응답 DTO로 변환한다.
    public static NearbyToiletResponseDto from(Toilet toilet, double distanceMeters) {
        return new NearbyToiletResponseDto(
                toilet.getId(),
                toilet.getName(),
                toilet.getMemo(),
                toilet.getAddress(),
                toilet.getXWgs(),
                toilet.getYWgs(),
                toilet.getStar(),
                toilet.getPlayer(),
                toilet.getGrade(),
                Math.round(distanceMeters)
        );
    }
}
