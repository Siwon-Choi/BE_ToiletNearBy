package com.toiletnearby.memo.service;

import com.toiletnearby.memo.domain.Memo;
import com.toiletnearby.memo.domain.vo.MemoContentsVo;
import com.toiletnearby.memo.domain.vo.MemoGoodVo;
import com.toiletnearby.memo.dto.MemoCreateDto;
import com.toiletnearby.memo.dto.MemoGoodUpdateDto;
import com.toiletnearby.memo.dto.MemoUpdateDto;
import com.toiletnearby.memo.repository.MemoRepository;
import com.toiletnearby.toilet.service.ToiletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// 메모 관련 비즈니스 흐름을 담당한다.
@Service
@RequiredArgsConstructor
@Transactional
public class MemoService {

    private final MemoRepository memoRepository;
    private final ToiletService toiletService;

    // 특정 화장실의 특정 memoId 메모를 조회한다.
    @Transactional(readOnly = true)
    public List<Memo> getMemo(Long toiletId, Long memoId) {
        validatePositive("toiletId", toiletId);
        validatePositive("memoId", memoId);

        return memoRepository.findByToiletIdAndMemoId(toiletId, memoId);
    }

    // 로그인한 사용자가 작성한 메모 목록을 조회한다.
    @Transactional(readOnly = true)
    public List<Memo> getMyMemos(String userId) {
        validateRequiredUserId(userId);

        return memoRepository.findAllByUserId(userId);
    }

    // 특정 화장실에 달린 모든 메모를 조회한다.
    @Transactional(readOnly = true)
    public List<Memo> getMemosByToiletId(Long toiletId) {
        validatePositive("toiletId", toiletId);

        return memoRepository.findAllByToiletId(toiletId);
    }

    // 메모를 생성하고, 화장실 평점 통계에도 반영한다.
    public Memo createMemo(MemoCreateDto dto) {
        MemoContentsVo contents = MemoContentsVo.from(dto.contents());
        MemoGoodVo good = MemoGoodVo.from(dto.good());

        Long memoId = memoRepository.countByToiletId(dto.toiletId()) + 1;

        Memo memo = Memo.create(
                dto.toiletId(),
                memoId,
                dto.userId(),
                contents,
                good
        );

        Memo savedMemo = memoRepository.save(memo);

        toiletService.applyMemoRating(dto.toiletId(), dto.good());

        return savedMemo;
    }

    // 본인 메모의 내용을 수정한다.
    public Long updateMemo(MemoUpdateDto dto) {
        Memo memo = getOwnedMemo(dto.toiletId(), dto.memoId(), dto.userId());

        memo.updateContents(dto.userId(), MemoContentsVo.from(dto.contents()));

        return memo.getId();
    }

    // 메모 평점을 수정한다.
    public void updateGood(MemoGoodUpdateDto dto) {
        Memo memo = getMemoOrThrow(dto.toiletId(), dto.memoId());

        memo.updateGood(MemoGoodVo.from(dto.good()));
    }

    // 본인 메모를 삭제한다.
    public Long deleteMemo(Long toiletId, Long memoId, String userId) {
        Memo memo = getOwnedMemo(toiletId, memoId, userId);

        memoRepository.deleteById(memo.getId());

        return memo.getId();
    }

    // toiletId + memoId로 메모를 찾는다.
    private Memo getMemoOrThrow(Long toiletId, Long memoId) {
        validatePositive("toiletId", toiletId);
        validatePositive("memoId", memoId);

        List<Memo> memos = memoRepository.findByToiletIdAndMemoId(toiletId, memoId);

        if (memos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "메모를 찾을 수 없습니다.");
        }

        return memos.get(0);
    }

    // 메모가 존재하고, 로그인 사용자가 작성자인지 확인한다.
    private Memo getOwnedMemo(Long toiletId, Long memoId, String userId) {
        validateRequiredUserId(userId);

        Memo memo = getMemoOrThrow(toiletId, memoId);

        if (!userId.equals(memo.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 메모만 수정할 수 있습니다.");
        }

        return memo;
    }

    private void validatePositive(String fieldName, Long value) {
        if (value == null || value < 1) {
            throw new IllegalArgumentException(fieldName + "는 1 이상이어야 합니다.");
        }
    }

    private void validateRequiredUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
    }
}
