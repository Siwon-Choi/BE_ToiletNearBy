package com.toiletnearby.toilet.service;

import com.toiletnearby.toilet.domain.Toilet;
import com.toiletnearby.toilet.dto.NearbyToiletResponseDto;
import com.toiletnearby.toilet.repository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// 화장실 관련 비즈니스 흐름을 담당한다.
@Service
@RequiredArgsConstructor
@Transactional
public class ToiletService {

    private final ToiletRepository toiletRepository;
    private final ToiletLocationCandidateService toiletLocationCandidateService;


    // 모든 화장실을 조회한다.
    @Transactional(readOnly = true)
    public List<Toilet> getToilets() {
        return toiletRepository.findAll();
    }

    // id로 화장실 하나를 조회한다.
    @Transactional(readOnly = true)
    public Toilet getToilet(Long toiletId) {
        validateRequiredToiletId(toiletId);

        return toiletRepository.findById(toiletId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "화장실을 찾을 수 없습니다."
                ));
    }


    // 기본 근처 검색은 grid cache 검색을 사용한다.
    @Transactional(readOnly = true)
    public List<NearbyToiletResponseDto> getToiletsNearBy(int rangeMeters, double userX, double userY) {
        return searchToiletsNearByIndexedGridCached(rangeMeters, userX, userY).toilets();
    }

    // 이전 방식: 전체 화장실 조회 후 거리 계산
    // 나중에 benchmark에서 최적화 전후 비교용으로 사용한다.
    @Transactional(readOnly = true)
    public NearbySearchResult searchToiletsNearByLegacy(int rangeMeters, double userX, double userY) {
        validateRangeMeters(rangeMeters);

        return toNearbySearchResult(
                toiletRepository.findAll(),
                rangeMeters,
                userX,
                userY
        );
    }

    // 개선 방식: DB에서 bounding box 후보를 먼저 조회한 뒤 거리 계산
    @Transactional(readOnly = true)
    public NearbySearchResult searchToiletsNearByIndexed(int rangeMeters, double userX, double userY) {
        validateRangeMeters(rangeMeters);

        return toNearbySearchResult(
                toiletLocationCandidateService.findIndexedCandidates(userY, userX, rangeMeters),
                rangeMeters,
                userX,
                userY
        );
    }

    // grid 좌표로 후보 조회를 캐싱한 뒤 실제 사용자 좌표 기준으로 거리 계산한다.
    @Transactional(readOnly = true)
    public NearbySearchResult searchToiletsNearByIndexedGridCached(int rangeMeters, double userX, double userY) {
        validateRangeMeters(rangeMeters);

        double gridLatitude = ToiletSearchGrid.snap(userY);
        double gridLongitude = ToiletSearchGrid.snap(userX);

        return toNearbySearchResult(
                // 후보 리스트 = 후보 리스트로 먼저 자르기 (캐싱 적용)
                toiletLocationCandidateService.findCandidates(gridLatitude, gridLongitude, rangeMeters),
                rangeMeters,
                userX,
                userY
        );
    }

    // 후보 목록에 정확한 거리 계산을 적용해서 최종 반경 안의 결과만 반환
    private NearbySearchResult toNearbySearchResult(
            List<Toilet> candidates,
            int rangeMeters,
            double userX,
            double userY
    ) {
        List<NearbyToiletResponseDto> nearbyToilets = candidates.stream()
                .map(toilet -> NearbyToiletResponseDto.from(
                        toilet,
                        Toilet.distance(toilet.getXWgs(), toilet.getYWgs(), userX, userY)
                ))
                .filter(toilet -> toilet.distanceMeters() < rangeMeters)
                .toList();

        return new NearbySearchResult(nearbyToilets, candidates.size());
    }

    // 메모 평점이 등록될 때 화장실 평점 통계에 반영한다.
    public void applyMemoRating(Long toiletId, int good) {
        Toilet toilet = getToilet(toiletId);

        toilet.addRating(good);
    }

    private void validateRequiredToiletId(Long toiletId) {
        if (toiletId == null) {
            throw new IllegalArgumentException("toiletId는 필수입니다.");
        }
    }

    private void validateRangeMeters(int rangeMeters) {
        if (rangeMeters < 1) {
            throw new IllegalArgumentException("rangeMeters는 1 이상이어야 합니다.");
        }
    }
}
