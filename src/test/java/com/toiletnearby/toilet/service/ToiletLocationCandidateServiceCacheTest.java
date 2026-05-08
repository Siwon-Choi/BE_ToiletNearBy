package com.toiletnearby.toilet.service;

import com.toiletnearby.global.config.CacheConfig;
import com.toiletnearby.toilet.domain.Toilet;
import com.toiletnearby.toilet.repository.ToiletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

// @Cacheable이 실제 Spring proxy를 통해 동작하는지 확인한다.
@SpringBootTest(classes = {
        CacheConfig.class,
        ToiletLocationCandidateService.class
})
class ToiletLocationCandidateServiceCacheTest {

    @Autowired
    private ToiletLocationCandidateService toiletLocationCandidateService;

    @MockitoBean
    private ToiletRepository toiletRepository;

    @Test
    @DisplayName("같은 grid 좌표와 반경으로 후보를 조회하면 캐시를 사용한다")
    void cacheCandidatesByGridCoordinate() {
        Toilet toilet = Toilet.create("서울역 화장실", 126.9723, 37.5559);

        given(toiletRepository.findWithinBoundingBox(
                anyDouble(),
                anyDouble(),
                anyDouble(),
                anyDouble()
        )).willReturn(List.of(toilet));

        double gridLatitude = ToiletSearchGrid.snap(37.555912);
        double gridLongitude = ToiletSearchGrid.snap(126.972312);

        List<Toilet> firstResult = toiletLocationCandidateService.findCandidates(
                gridLatitude,
                gridLongitude,
                2000
        );

        List<Toilet> secondResult = toiletLocationCandidateService.findCandidates(
                gridLatitude,
                gridLongitude,
                2000
        );

        assertThat(firstResult).containsExactly(toilet);
        assertThat(secondResult).containsExactly(toilet);

        then(toiletRepository).should().findWithinBoundingBox(
                anyDouble(),
                anyDouble(),
                anyDouble(),
                anyDouble()
        );
    }
}
