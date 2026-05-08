package com.toiletnearby.memo.repository.jpa;

import com.toiletnearby.memo.domain.Memo;
import com.toiletnearby.memo.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// MemoRepository 인터페이스를 JPA 방식으로 구현하는 Adapter다.
// Service는 이 클래스가 아니라 MemoRepository 인터페이스에만 의존한다.
@Repository
@RequiredArgsConstructor
public class MemoRepositoryJpaAdapter implements MemoRepository {

    private final MemoJpaRepository memoJpaRepository;

    @Override
    public Optional<Memo> findById(Long id) {
        return memoJpaRepository.findById(id);
    }

    @Override
    public Memo save(Memo memo) {
        return memoJpaRepository.save(memo);
    }

    @Override
    public void deleteById(Long id) {
        memoJpaRepository.deleteById(id);
    }

    @Override
    public List<Memo> findAllByToiletId(Long toiletId) {
        return memoJpaRepository.findAllByToiletId(toiletId);
    }

    @Override
    public List<Memo> findByToiletIdAndMemoId(Long toiletId, Long memoId) {
        return memoJpaRepository.findByToiletIdAndMemoId(toiletId, memoId);
    }

    @Override
    public List<Memo> findAllByUserId(String userId) {
        return memoJpaRepository.findAllByUserId(userId);
    }

    @Override
    public long countByToiletId(Long toiletId) {
        return memoJpaRepository.countByToiletId(toiletId);
    }
}
