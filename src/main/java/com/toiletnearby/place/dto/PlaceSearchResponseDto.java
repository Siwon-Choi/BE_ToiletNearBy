package com.toiletnearby.place.dto;

// Kakao 장소 검색 결과를 클라이언트에 내려주는 응답 DTO다.
public record PlaceSearchResponseDto(
        String id,
        String name,
        String categoryName,
        String phone,
        String address,
        String roadAddress,
        double x,
        double y,
        String placeUrl,
        int distance
) {
}
