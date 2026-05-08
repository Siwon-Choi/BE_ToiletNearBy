package com.toiletnearby.memo.dto;

import com.toiletnearby.memo.domain.Memo;
import com.toiletnearby.memo.domain.vo.MemoContentsVo;
import com.toiletnearby.memo.domain.vo.MemoGoodVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// MemoResponseDto 변환 규칙을 테스트한다.
class MemoResponseDtoTest {

    @Test
    @DisplayName("Memo 엔티티를 응답 DTO로 변환한다")
    void fromMemo() {
        Memo memo = Memo.create(
                1L,
                1L,
                "tester",
                MemoContentsVo.from("깨끗합니다."),
                MemoGoodVo.from(5)
        );

        MemoResponseDto response = MemoResponseDto.from(memo);

        assertThat(response.toiletId()).isEqualTo(1L);
        assertThat(response.memoId()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo("tester");
        assertThat(response.contents()).isEqualTo("깨끗합니다.");
        assertThat(response.good()).isEqualTo(5);
    }
}
