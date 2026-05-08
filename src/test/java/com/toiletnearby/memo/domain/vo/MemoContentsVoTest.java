package com.toiletnearby.memo.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoContentsVoTest {

    @Test
    @DisplayName("메모 내용을 생성한다")
    void createMemoContents() {
        MemoContentsVo contents = MemoContentsVo.from("깨끗한 화장실입니다.");

        assertThat(contents.getValue()).isEqualTo("깨끗한 화장실입니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("메모 내용은 비어 있을 수 없다")
    void contentsIsRequired(String value) {
        assertThatThrownBy(() -> MemoContentsVo.from(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("contents는 필수입니다.");
    }

    @Test
    @DisplayName("메모 내용은 500자를 넘을 수 없다")
    void contentsMaxLength() {
        String value = "a".repeat(501);

        assertThatThrownBy(() -> MemoContentsVo.from(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("contents는 500자 이하입니다.");
    }
}
