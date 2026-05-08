package com.toiletnearby.place.controller;

import com.toiletnearby.place.dto.PlaceSearchResponseDto;
import com.toiletnearby.place.service.PlaceSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 장소 검색 API를 담당한다.
@RestController
@RequiredArgsConstructor
public class PlaceSearchController {

    private final PlaceSearchService placeSearchService;

    // Kakao keyword search API를 이용해 장소를 검색한다.
    @GetMapping("/api/places/search")
    public List<PlaceSearchResponseDto> searchPlaces(
            @RequestParam String query,
            // 확장성 고려
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Double x,
            @RequestParam(required = false) Double y,
            @RequestParam(required = false) Integer radius
    ) {
        return placeSearchService.search(query, page, size, x, y, radius);
    }
}
