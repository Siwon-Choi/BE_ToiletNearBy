package com.toiletnearby.memo.service;

import com.toiletnearby.memo.domain.Memo;
import com.toiletnearby.memo.domain.vo.MemoContentsVo;
import com.toiletnearby.memo.domain.vo.MemoGoodVo;
import com.toiletnearby.memo.dto.MemoCreateDto;
import com.toiletnearby.memo.dto.MemoGoodUpdateDto;
import com.toiletnearby.memo.dto.MemoUpdateDto;
import com.toiletnearby.memo.repository.MemoRepository;
import com.toiletnearby.toilet.service.ToiletService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MemoServiceTest {

    @Mock
    private MemoRepository memoRepository;

    @Mock
    private ToiletService toiletService;

    @InjectMocks
    private MemoService memoService;

    @Test
    @DisplayName("특정 화장실의 특정 메모를 조회한다")
    void getMemo() {
        Memo memo = createMemo(1L, 1L, "tester", "깨끗합니다.", 5);

        given(memoRepository.findByToiletIdAndMemoId(1L, 1L))
                .willReturn(List.of(memo));

        List<Memo> result = memoService.getMemo(1L, 1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContents()).isEqualTo("깨끗합니다.");
    }

    @Test
    @DisplayName("내가 작성한 메모 목록을 조회한다")
    void getMyMemos() {
        Memo memo = createMemo(1L, 1L, "tester", "내 메모", 5);

        given(memoRepository.findAllByUserId("tester")).willReturn(List.of(memo));

        List<Memo> result = memoService.getMyMemos("tester");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("tester");
    }

    @Test
    @DisplayName("메모를 생성하고 화장실 평점에 반영한다")
    void createMemo() {
        MemoCreateDto dto = new MemoCreateDto(1L, "tester", "깨끗합니다.", 5);

        given(memoRepository.countByToiletId(1L)).willReturn(0L);
        given(memoRepository.save(any(Memo.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Memo result = memoService.createMemo(dto);

        assertThat(result.getToiletId()).isEqualTo(1L);
        assertThat(result.getMemoId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo("tester");
        assertThat(result.getContents()).isEqualTo("깨끗합니다.");
        assertThat(result.getGood()).isEqualTo(5);

        then(toiletService).should().applyMemoRating(1L, 5);
    }

    @Test
    @DisplayName("본인 메모의 내용을 수정한다")
    void updateMemo() {
        Memo memo = createMemo(1L, 1L, "tester", "수정 전", 5);
        MemoUpdateDto dto = new MemoUpdateDto(1L, 1L, "tester", "수정 후");

        given(memoRepository.findByToiletIdAndMemoId(1L, 1L))
                .willReturn(List.of(memo));

        memoService.updateMemo(dto);

        assertThat(memo.getContents()).isEqualTo("수정 후");
    }

    @Test
    @DisplayName("다른 사람의 메모는 수정할 수 없다")
    void cannotUpdateOtherUsersMemo() {
        Memo memo = createMemo(1L, 1L, "owner", "수정 전", 5);
        MemoUpdateDto dto = new MemoUpdateDto(1L, 1L, "attacker", "수정 시도");

        given(memoRepository.findByToiletIdAndMemoId(1L, 1L))
                .willReturn(List.of(memo));

        assertThatThrownBy(() -> memoService.updateMemo(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("본인 메모만 수정할 수 있습니다.");
    }

    @Test
    @DisplayName("메모 평점을 수정한다")
    void updateGood() {
        Memo memo = createMemo(1L, 1L, "tester", "깨끗합니다.", 3);
        MemoGoodUpdateDto dto = new MemoGoodUpdateDto(1L, 1L, 5);

        given(memoRepository.findByToiletIdAndMemoId(1L, 1L))
                .willReturn(List.of(memo));

        memoService.updateGood(dto);

        assertThat(memo.getGood()).isEqualTo(5);
    }

    @Test
    @DisplayName("본인 메모를 삭제한다")
    void deleteMemo() {
        Memo memo = createMemo(1L, 1L, "tester", "삭제할 메모", 5);

        given(memoRepository.findByToiletIdAndMemoId(1L, 1L))
                .willReturn(List.of(memo));

        memoService.deleteMemo(1L, 1L, "tester");

        then(memoRepository).should().deleteById(memo.getId());
    }

    @Test
    @DisplayName("존재하지 않는 메모를 수정하면 예외가 발생한다")
    void cannotUpdateUnknownMemo() {
        MemoUpdateDto dto = new MemoUpdateDto(1L, 99L, "tester", "수정 시도");

        given(memoRepository.findByToiletIdAndMemoId(1L, 99L))
                .willReturn(List.of());

        assertThatThrownBy(() -> memoService.updateMemo(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("메모를 찾을 수 없습니다.");
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
