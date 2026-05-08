package com.toiletnearby.memo.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoGoodVoTest {

    @Test
    @DisplayName("메모 평점 값을 생성한다")
    void createMemoGood() {
        MemoGoodVo good = MemoGoodVo.from(5);

        assertThat(good.getValue()).isEqualTo(5);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 6})
    @DisplayName("메모 평점 값은 1부터 5까지만 가능하다")
    void goodRange(int value) {
        assertThatThrownBy(() -> MemoGoodVo.from(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("good은 1부터 5까지 가능합니다.");
    }
}
