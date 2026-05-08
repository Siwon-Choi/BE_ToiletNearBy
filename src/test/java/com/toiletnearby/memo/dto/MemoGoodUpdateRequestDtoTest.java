package com.toiletnearby.memo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// MemoGoodUpdateRequestDto 변환 규칙을 테스트한다.
class MemoGoodUpdateRequestDtoTest {

    @Test
    @DisplayName("메모 평점 수정 DTO로 변환할 때 path 값을 사용한다")
    void toGoodUpdateDto() {
        MemoGoodUpdateRequestDto requestDto = new MemoGoodUpdateRequestDto(4);

        MemoGoodUpdateDto serviceDto = requestDto.toGoodUpdateDto(1L, 2L);

        assertThat(serviceDto.toiletId()).isEqualTo(1L);
        assertThat(serviceDto.memoId()).isEqualTo(2L);
        assertThat(serviceDto.good()).isEqualTo(4);
    }
}
