package com.toiletnearby.memo.repository.jpa;

import com.toiletnearby.memo.domain.Memo;
import com.toiletnearby.memo.domain.vo.MemoContentsVo;
import com.toiletnearby.memo.domain.vo.MemoGoodVo;
import com.toiletnearby.memo.repository.MemoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// MemoRepository Adapter가 실제 JPA 저장소와 잘 연결되는지 확인한다.
@DataJpaTest
@Import(MemoRepositoryJpaAdapter.class)
class MemoRepositoryJpaAdapterTest {

    @Autowired
    private MemoRepository memoRepository;

    @Test
    @DisplayName("메모를 저장하고 id로 조회한다")
    void saveAndFindById() {
        Memo memo = createMemo(1L, 1L, "tester", "깨끗합니다.", 5);

        Memo savedMemo = memoRepository.save(memo);

        Optional<Memo> foundMemo = memoRepository.findById(savedMemo.getId());

        assertThat(foundMemo).isPresent();
        assertThat(foundMemo.get().getUserId()).isEqualTo("tester");
        assertThat(foundMemo.get().getContents()).isEqualTo("깨끗합니다.");
    }

    @Test
    @DisplayName("특정 화장실의 메모 목록을 조회한다")
    void findAllByToiletId() {
        memoRepository.save(createMemo(1L, 1L, "tester1", "1번 화장실 메모", 5));
        memoRepository.save(createMemo(1L, 2L, "tester2", "1번 화장실 다른 메모", 4));
        memoRepository.save(createMemo(2L, 1L, "tester3", "2번 화장실 메모", 3));

        List<Memo> memos = memoRepository.findAllByToiletId(1L);

        assertThat(memos).hasSize(2);
        assertThat(memos).extracting(Memo::getToiletId).containsOnly(1L);
    }

    @Test
    @DisplayName("특정 화장실의 특정 memoId 메모를 조회한다")
    void findByToiletIdAndMemoId() {
        memoRepository.save(createMemo(1L, 1L, "tester1", "첫 번째 메모", 5));
        memoRepository.save(createMemo(1L, 2L, "tester2", "두 번째 메모", 4));

        List<Memo> memos = memoRepository.findByToiletIdAndMemoId(1L, 2L);

        assertThat(memos).hasSize(1);
        assertThat(memos.get(0).getContents()).isEqualTo("두 번째 메모");
    }

    @Test
    @DisplayName("특정 사용자가 작성한 메모 목록을 조회한다")
    void findAllByUserId() {
        memoRepository.save(createMemo(1L, 1L, "tester", "내 메모 1", 5));
        memoRepository.save(createMemo(2L, 1L, "tester", "내 메모 2", 4));
        memoRepository.save(createMemo(3L, 1L, "other", "다른 사람 메모", 3));

        List<Memo> memos = memoRepository.findAllByUserId("tester");

        assertThat(memos).hasSize(2);
        assertThat(memos).extracting(Memo::getUserId).containsOnly("tester");
    }

    @Test
    @DisplayName("특정 화장실에 달린 메모 개수를 센다")
    void countByToiletId() {
        memoRepository.save(createMemo(1L, 1L, "tester1", "메모 1", 5));
        memoRepository.save(createMemo(1L, 2L, "tester2", "메모 2", 4));
        memoRepository.save(createMemo(2L, 1L, "tester3", "다른 화장실 메모", 3));

        long count = memoRepository.countByToiletId(1L);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("메모를 삭제한다")
    void deleteById() {
        Memo savedMemo = memoRepository.save(createMemo(1L, 1L, "tester", "삭제할 메모", 5));

        memoRepository.deleteById(savedMemo.getId());

        assertThat(memoRepository.findById(savedMemo.getId())).isEmpty();
    }

    private Memo createMemo(Long toiletId, Long memoId, String userId, String contents, int good) {
        return Memo.create(
                toiletId,
                memoId,
                userId,
                MemoContentsVo.from(contents),
                MemoGoodVo.from(good)
        );
    }
}
