package com.toiletnearby.toilet.service;

import com.toiletnearby.global.config.CacheConfig;
import com.toiletnearby.toilet.domain.Toilet;
import com.toiletnearby.toilet.repository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// DB에서 후보 화장실을 줄여오는 조회 (사각형)
@Service
@RequiredArgsConstructor
public class ToiletLocationCandidateService {

    // 지구의 위도 1도 = 111320m
    private static final double METERS_PER_LATITUDE_DEGREE = 111320.0;

    private final ToiletRepository toiletRepository;

    // 사용자 좌표와 반경을 사각형 범위로 바꾼 뒤 DB에서 후보만 조회한다.
    @Transactional(readOnly = true)
    public List<Toilet> findIndexedCandidates(double latitude, double longitude, int rangeMeters) {
        Bounds bounds = Bounds.from(latitude, longitude, rangeMeters);

        return toiletRepository.findWithinBoundingBox(
                bounds.minLongitude(),
                bounds.maxLongitude(),
                bounds.minLatitude(),
                bounds.maxLatitude()
        );
    }

    // grid 좌표와 반경을 기준으로 후보 목록을 캐시한다.
    // 같은 grid 좌표 요청은 DB를 다시 조회하지 않고 캐시된 후보를 재사용한다.
    @Cacheable(
            cacheNames = CacheConfig.NEARBY_TOILET_CANDIDATES,
            key = "'lat=' + #gridLatitude + ':lng=' + #gridLongitude + ':range=' + #rangeMeters"
    )
    @Transactional(readOnly = true)
    public List<Toilet> findCandidates(double gridLatitude, double gridLongitude, int rangeMeters) {
        Bounds bounds = Bounds.from(
                gridLatitude,
                gridLongitude,
                rangeMeters + ToiletSearchGrid.GRID_DIAGONAL_METERS
        );

        return toiletRepository.findWithinBoundingBox(
                bounds.minLongitude(),
                bounds.maxLongitude(),
                bounds.minLatitude(),
                bounds.maxLatitude()
        );
    }

    // 위도/경도 사각형 범위를 표현한다.
    private record Bounds(
            double minLongitude,
            double maxLongitude,
            double minLatitude,
            double maxLatitude
    ) {

        // meter 반경을 위도/경도 차이로 변환한다.
        private static Bounds from(double latitude, double longitude, double rangeMeters) {

            // 제한 거리 / 111320 = 제한 거리를 위도 크기로 변환
            double latitudeDelta = rangeMeters / METERS_PER_LATITUDE_DEGREE;

            // 경도 1도 (항상 적도 기준 1도가 아님)
            double longitudeDivider = METERS_PER_LATITUDE_DEGREE * Math.max(
                    Math.cos(Math.toRadians(latitude)),
                    0.000001
                    // 0이 되는 것 방지
            );

            // 제한 거리 / 경도 1도에 해당 길이 = 제한 거리를 경도 크기로 변환
            double longitudeDelta = rangeMeters / longitudeDivider;

            // 사각형 기준 정리
            return new Bounds(
                    longitude - longitudeDelta,
                    longitude + longitudeDelta,
                    latitude - latitudeDelta,
                    latitude + latitudeDelta
            );
        }
    }
}
