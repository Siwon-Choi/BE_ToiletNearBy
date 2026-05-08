package com.toiletnearby.password.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordCreateRequestDtoTest {

    @Test
    @DisplayName("비밀번호 생성 요청 DTO를 Service DTO로 변환한다")
    void toSaveDto() {
        PasswordCreateRequestDto requestDto = new PasswordCreateRequestDto(1L, "1234");

        PasswordSaveDto saveDto = requestDto.toSaveDto("tester");

        assertThat(saveDto.toiletId()).isEqualTo(1L);
        assertThat(saveDto.userId()).isEqualTo("tester");
        assertThat(saveDto.password()).isEqualTo("1234");
    }
}
