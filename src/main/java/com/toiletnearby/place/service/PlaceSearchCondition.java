package com.toiletnearby.place.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// 장소 검색에 필요한 조건을 하나로 묶는다.
public record PlaceSearchCondition(
        String query,
        int page,
        int size,
        Double x,
        Double y,
        Integer radius
) {

    // Controller에서 받은 요청 값을 검증된 검색 조건으로 바꾼다.
    public static PlaceSearchCondition of(
            String query,
            int page,
            int size,
            Double x,
            Double y,
            Integer radius
    ) {
        validate(query, page, size, x, y, radius);

        return new PlaceSearchCondition(
                query.trim(),
                page,
                size,
                x,
                y,
                radius
        );
    }

    // Kakao keyword search API의 요청 제한에 맞춰 검증한다.
    private static void validate(String query, int page, int size, Double x, Double y, Integer radius) {
        if (query == null || query.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "검색어가 필요합니다.");
        }

        if (page < 1 || page > 45) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page는 1부터 45까지 가능합니다.");
        }

        if (size < 1 || size > 15) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size는 1부터 15까지 가능합니다.");
        }

        if ((x == null) != (y == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "x와 y는 함께 전달해야 합니다.");
        }

        if (radius != null && x == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "radius를 사용하려면 x와 y가 필요합니다.");
        }

        if (radius != null && (radius < 0 || radius > 20000)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "radius는 0부터 20000까지 가능합니다.");
        }
    }

    // 위치 기반 검색인지 확인한다.
    public boolean hasCoordinate() {
        return x != null && y != null;
    }
}
