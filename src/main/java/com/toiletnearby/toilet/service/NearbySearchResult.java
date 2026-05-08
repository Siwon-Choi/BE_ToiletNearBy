package com.toiletnearby.toilet.service;

import com.toiletnearby.toilet.dto.NearbyToiletResponseDto;

import java.util.List;

// 근처 화장실 검색 결과와 후보 개수를 함께 담는다.
public record NearbySearchResult(
        List<NearbyToiletResponseDto> toilets,// 벤치마크용
        int candidateCount
) {
}
