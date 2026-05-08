package com.toiletnearby.toilet.repository.jpa;

import com.toiletnearby.toilet.domain.Toilet;
import com.toiletnearby.toilet.repository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ToiletRepository 인터페이스를 JPA 방식으로 구현
@Repository
@RequiredArgsConstructor
public class ToiletRepositoryJpaAdapter implements ToiletRepository {

    private final ToiletJpaRepository toiletJpaRepository;

    @Override
    public List<Toilet> findAll() {
        return toiletJpaRepository.findAll();
    }

    @Override
    public Optional<Toilet> findById(Long id) {
        return toiletJpaRepository.findById(id);
    }

    @Override
    public List<Toilet> findWithinBoundingBox(
            double minLongitude,
            double maxLongitude,
            double minLatitude,
            double maxLatitude
    ) {
        return toiletJpaRepository.findWithinBoundingBox(
                minLongitude,
                maxLongitude,
                minLatitude,
                maxLatitude
        );
    }

    @Override
    public void saveAll(Iterable<Toilet> toilets) {
        toiletJpaRepository.saveAll(toilets);
    }

    @Override
    public Toilet save(Toilet toilet) {
        return toiletJpaRepository.save(toilet);
    }

    @Override
    public void deleteAllInBatch() {
        toiletJpaRepository.deleteAllInBatch();
    }

    @Override
    public long count() {
        return toiletJpaRepository.count();
    }
}
