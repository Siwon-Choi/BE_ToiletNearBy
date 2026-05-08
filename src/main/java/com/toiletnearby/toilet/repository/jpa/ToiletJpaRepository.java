package com.toiletnearby.toilet.repository.jpa;

import com.toiletnearby.toilet.domain.Toilet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// Spring Data JPA 전용 Repository다.
public interface ToiletJpaRepository extends JpaRepository<Toilet, Long> {

    @Query("""
            select t
            from Toilet t
            where t.xWgs between :minLongitude and :maxLongitude
              and t.yWgs between :minLatitude and :maxLatitude
            """)
    List<Toilet> findWithinBoundingBox(
            @Param("minLongitude") double minLongitude,
            @Param("maxLongitude") double maxLongitude,
            @Param("minLatitude") double minLatitude,
            @Param("maxLatitude") double maxLatitude
    );
}
