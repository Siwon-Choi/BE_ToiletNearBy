package com.toiletnearby.place.client;

import com.toiletnearby.place.dto.PlaceSearchResponseDto;
import com.toiletnearby.place.service.PlaceSearchCondition;

import java.util.List;

// 외부 장소 검색 provider를 추상화한 포트다.
// 현재 구현체는 Kakao지만, 나중에 Google Places 같은 구현체를 추가해도 Service는 이 인터페이스만 의존한다.
public interface PlaceSearchClient {

    // 검증된 검색 조건으로 외부 장소 검색 결과를 조회한다.
    List<PlaceSearchResponseDto> search(PlaceSearchCondition condition);
}
