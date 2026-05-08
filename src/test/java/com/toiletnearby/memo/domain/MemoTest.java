package com.toiletnearby.memo.domain;

import com.toiletnearby.memo.domain.vo.MemoContentsVo;
import com.toiletnearby.memo.domain.vo.MemoGoodVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoTest {

    @Test
    @DisplayName("메모를 생성한다")
    void createMemo() {
        Memo memo = Memo.create(
                1L,
                1L,
                "tester",
                MemoContentsVo.from("깨끗한 화장실입니다."),
                MemoGoodVo.from(5)
        );

        assertThat(memo.getToiletId()).isEqualTo(1L);
        assertThat(memo.getMemoId()).isEqualTo(1L);
        assertThat(memo.getUserId()).isEqualTo("tester");
        assertThat(memo.getContents()).isEqualTo("깨끗한 화장실입니다.");
        assertThat(memo.getGood()).isEqualTo(5);
    }

    @Test
    @DisplayName("toiletId는 1 이상이어야 한다")
    void toiletIdIsPositive() {
        assertThatThrownBy(() -> Memo.create(
                0L,
                1L,
                "tester",
                MemoContentsVo.from("내용"),
                MemoGoodVo.from(5)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("toiletId는 1 이상이어야 합니다.");
    }

    @Test
    @DisplayName("memoId는 1 이상이어야 한다")
    void memoIdIsPositive() {
        assertThatThrownBy(() -> Memo.create(
                1L,
                0L,
                "tester",
                MemoContentsVo.from("내용"),
                MemoGoodVo.from(5)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("memoId는 1 이상이어야 합니다.");
    }

    @Test
    @DisplayName("userId는 필수다")
    void userIdIsRequired() {
        assertThatThrownBy(() -> Memo.create(
                1L,
                1L,
                " ",
                MemoContentsVo.from("내용"),
                MemoGoodVo.from(5)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userId는 필수입니다.");
    }

    @Test
    @DisplayName("메모 내용을 수정한다")
    void updateContents() {
        Memo memo = Memo.create(
                1L,
                1L,
                "tester",
                MemoContentsVo.from("기존 내용"),
                MemoGoodVo.from(3)
        );

        memo.updateContents("tester", MemoContentsVo.from("수정 내용"));

        assertThat(memo.getContents()).isEqualTo("수정 내용");
    }

    @Test
    @DisplayName("메모 평점 값을 수정한다")
    void updateGood() {
        Memo memo = Memo.create(
                1L,
                1L,
                "tester",
                MemoContentsVo.from("내용"),
                MemoGoodVo.from(3)
        );

        memo.updateGood(MemoGoodVo.from(5));

        assertThat(memo.getGood()).isEqualTo(5);
    }
}
