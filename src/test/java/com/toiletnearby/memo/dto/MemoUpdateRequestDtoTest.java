package com.toiletnearby.memo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// MemoUpdateRequestDto 변환 규칙을 테스트한다.
class MemoUpdateRequestDtoTest {

    @Test
    @DisplayName("메모 수정 DTO로 변환할 때 path 값과 인증 사용자 id를 사용한다")
    void toUpdateDto() {
        MemoUpdateRequestDto requestDto = new MemoUpdateRequestDto("수정 후");

        MemoUpdateDto serviceDto = requestDto.toUpdateDto(1L, 2L, "tester");

        assertThat(serviceDto.toiletId()).isEqualTo(1L);
        assertThat(serviceDto.memoId()).isEqualTo(2L);
        assertThat(serviceDto.userId()).isEqualTo("tester");
        assertThat(serviceDto.contents()).isEqualTo("수정 후");
    }
}
