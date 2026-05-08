package com.toiletnearby.place.service;

import com.toiletnearby.place.client.PlaceSearchClient;
import com.toiletnearby.place.dto.PlaceSearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// 장소 검색 유스케이스를 담당한다.
@Service
@RequiredArgsConstructor
public class PlaceSearchService {

    private final PlaceSearchClient placeSearchClient;

    // 요청 값을 검증한 뒤 외부 장소 검색 client에 위임한다.
    public List<PlaceSearchResponseDto> search(
            String query,
            int page,
            int size,
            Double x,
            Double y,
            Integer radius
    ) {
        PlaceSearchCondition condition = PlaceSearchCondition.of(query, page, size, x, y, radius);

        return placeSearchClient.search(condition);
    }
}
