package com.toiletnearby.toilet.service;

import com.toiletnearby.toilet.domain.Toilet;
import com.toiletnearby.toilet.repository.ToiletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

// Bounding Box 후보 조회 로직을 테스트한다.
@ExtendWith(MockitoExtension.class)
class ToiletLocationCandidateServiceTest {

    @Mock
    private ToiletRepository toiletRepository;

    @InjectMocks
    private ToiletLocationCandidateService toiletLocationCandidateService;

    @Test
    @DisplayName("사용자 좌표와 반경으로 bounding box 후보를 조회한다")
    void findIndexedCandidates() {
        Toilet toilet = Toilet.create("서울역 화장실", 126.9723, 37.5559);

        given(toiletRepository.findWithinBoundingBox(
                anyDouble(),
                anyDouble(),
                anyDouble(),
                anyDouble()
        )).willReturn(List.of(toilet));

        List<Toilet> result = toiletLocationCandidateService.findIndexedCandidates(
                37.5559,
                126.9723,
                1000
        );

        assertThat(result).containsExactly(toilet);

        ArgumentCaptor<Double> minLongitude = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> maxLongitude = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> minLatitude = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> maxLatitude = ArgumentCaptor.forClass(Double.class);

        then(toiletRepository).should().findWithinBoundingBox(
                minLongitude.capture(),
                maxLongitude.capture(),
                minLatitude.capture(),
                maxLatitude.capture()
        );

        assertThat(minLongitude.getValue()).isLessThan(126.9723);
        assertThat(maxLongitude.getValue()).isGreaterThan(126.9723);
        assertThat(minLatitude.getValue()).isLessThan(37.5559);
        assertThat(maxLatitude.getValue()).isGreaterThan(37.5559);
    }
}
