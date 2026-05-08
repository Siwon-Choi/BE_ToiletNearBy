package com.toiletnearby.memo.repository.jpa;

import com.toiletnearby.memo.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoJpaRepository extends JpaRepository<Memo, Long> {

    List<Memo> findAllByToiletId(Long toiletId);

    List<Memo> findByToiletIdAndMemoId(Long toiletId, Long memoId);

    List<Memo> findAllByUserId(String userId);
    
    long countByToiletId(Long toiletId);
}
