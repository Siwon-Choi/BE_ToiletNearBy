package com.toiletnearby.toilet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toiletnearby.toilet.domain.Toilet;

// 화장실 기본 조회 응답 DTO다.
public record ToiletResponseDto(
        Long id,
        String name,
        String memo,
        String address,
        @JsonProperty("x_wgs") double xWgs,
        @JsonProperty("y_wgs") double yWgs,
        int star,
        int player,
        float grade
) {

    // Toilet 엔티티를 API 응답 DTO로 변환한다.
    public static ToiletResponseDto from(Toilet toilet) {
        return new ToiletResponseDto(
                toilet.getId(),
                toilet.getName(),
                toilet.getMemo(),
                toilet.getAddress(),
                toilet.getXWgs(),
                toilet.getYWgs(),
                toilet.getStar(),
                toilet.getPlayer(),
                toilet.getGrade()
        );
    }
}
