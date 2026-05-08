package com.toiletnearby.password.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordUpdateRequestDtoTest {

    @Test
    @DisplayName("비밀번호 수정 요청 DTO를 path 값과 인증 사용자 id를 포함한 Service DTO로 변환한다")
    void toSaveDto() {
        PasswordUpdateRequestDto requestDto = new PasswordUpdateRequestDto("5678");

        PasswordSaveDto saveDto = requestDto.toSaveDto(1L, "tester");

        assertThat(saveDto.toiletId()).isEqualTo(1L);
        assertThat(saveDto.userId()).isEqualTo("tester");
        assertThat(saveDto.password()).isEqualTo("5678");
    }
}
