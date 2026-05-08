package com.toiletnearby.memo.repository;

import com.toiletnearby.memo.domain.Memo;

import java.util.List;
import java.util.Optional;

public interface MemoRepository {

    Optional<Memo> findById(Long id);

    Memo save(Memo memo);

    void deleteById(Long id);

    List<Memo> findAllByToiletId(Long toiletId);

    List<Memo> findByToiletIdAndMemoId(Long toiletId, Long memoId);

    List<Memo> findAllByUserId(String userId);
    
    long countByToiletId(Long toiletId);
}
