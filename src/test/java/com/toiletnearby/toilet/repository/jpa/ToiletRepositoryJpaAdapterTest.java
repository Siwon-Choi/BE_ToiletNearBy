package com.toiletnearby.toilet.repository.jpa;

import com.toiletnearby.toilet.domain.Toilet;
import com.toiletnearby.toilet.repository.ToiletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// ToiletRepository Adapter가 실제 JPA 저장소와 잘 연결되는지 확인한다.
@DataJpaTest
@Import(ToiletRepositoryJpaAdapter.class)
class ToiletRepositoryJpaAdapterTest {

    @Autowired
    private ToiletRepository toiletRepository;

    @Test
    @DisplayName("화장실을 저장하고 id로 조회한다")
    void saveAndFindById() {
        Toilet toilet = Toilet.create("서울역 화장실", 126.9723, 37.5559);

        Toilet savedToilet = toiletRepository.save(toilet);

        Optional<Toilet> foundToilet = toiletRepository.findById(savedToilet.getId());

        assertThat(foundToilet).isPresent();
        assertThat(foundToilet.get().getName()).isEqualTo("서울역 화장실");
    }

    @Test
    @DisplayName("모든 화장실을 조회한다")
    void findAll() {
        toiletRepository.save(Toilet.create("서울역 화장실", 126.9723, 37.5559));
        toiletRepository.save(Toilet.create("시청역 화장실", 126.9784, 37.5665));

        List<Toilet> toilets = toiletRepository.findAll();

        assertThat(toilets).hasSize(2);
    }

    @Test
    @DisplayName("경도와 위도 범위 안의 화장실 후보만 조회한다")
    void findWithinBoundingBox() {
        toiletRepository.save(Toilet.create("서울역 화장실", 126.9723, 37.5559));
        toiletRepository.save(Toilet.create("시청역 화장실", 126.9784, 37.5665));
        toiletRepository.save(Toilet.create("부산역 화장실", 129.0396, 35.1151));

        List<Toilet> toilets = toiletRepository.findWithinBoundingBox(
                126.9,
                127.0,
                37.5,
                37.6
        );

        assertThat(toilets).hasSize(2);
        assertThat(toilets).extracting(Toilet::getName)
                .containsExactlyInAnyOrder("서울역 화장실", "시청역 화장실");
    }

    @Test
    @DisplayName("여러 화장실을 한 번에 저장하고 개수를 센다")
    void saveAllAndCount() {
        List<Toilet> toilets = List.of(
                Toilet.create("서울역 화장실", 126.9723, 37.5559),
                Toilet.create("시청역 화장실", 126.9784, 37.5665)
        );

        toiletRepository.saveAll(toilets);

        assertThat(toiletRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("모든 화장실을 batch로 삭제한다")
    void deleteAllInBatch() {
        toiletRepository.save(Toilet.create("서울역 화장실", 126.9723, 37.5559));
        toiletRepository.save(Toilet.create("시청역 화장실", 126.9784, 37.5665));

        toiletRepository.deleteAllInBatch();

        assertThat(toiletRepository.count()).isZero();
    }
}
