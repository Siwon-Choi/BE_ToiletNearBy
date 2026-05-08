package com.toiletnearby.memo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// MemoCreateRequestDto 변환 규칙을 테스트한다.
class MemoCreateRequestDtoTest {

    @Test
    @DisplayName("메모 생성 DTO로 변환할 때 인증 사용자 id를 사용한다")
    void toCreateDto() {
        MemoCreateRequestDto requestDto = new MemoCreateRequestDto(
                1L,
                "깨끗합니다.",
                5
        );

        MemoCreateDto serviceDto = requestDto.toCreateDto("tester");

        assertThat(serviceDto.toiletId()).isEqualTo(1L);
        assertThat(serviceDto.userId()).isEqualTo("tester");
        assertThat(serviceDto.contents()).isEqualTo("깨끗합니다.");
        assertThat(serviceDto.good()).isEqualTo(5);
    }
}
