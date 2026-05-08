package com.toiletnearby.toilet.repository;

import com.toiletnearby.toilet.domain.Toilet;

import java.util.List;
import java.util.Optional;


public interface ToiletRepository {

    List<Toilet> findAll();

    Optional<Toilet> findById(Long id);

    // 지도 검색 최적화를 위해 사각형 범위 안의 후보 화장실만 조회
    List<Toilet> findWithinBoundingBox(
            double minLongitude,
            double maxLongitude,
            double minLatitude,
            double maxLatitude
    );

    // CSV import 때 여러 화장실을 한 번에 저장한다.
    // 호출부에서는 저장된 Entity 목록을 다시 사용하지 않으므로 명령처럼 void로 둔다.
    void saveAll(Iterable<Toilet> toilets);

    // 화장실 하나를 저장
    Toilet save(Toilet toilet);

    // import replace 옵션에서 기존 데이터를 한 번에 삭제
    void deleteAllInBatch();

    // 저장된 화장실 개수를 센다.
    long count();
}
