package com.toiletnearby.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserLoginResponseDtoTest {

    @Test
    @DisplayName("로그인 결과 DTO로 로그인 응답 DTO를 생성한다")
    void createResponseFromLoginResult() {
        UserLoginResultDto resultDto = new UserLoginResultDto("tester", "access-token");

        UserLoginResponseDto responseDto = UserLoginResponseDto.from(resultDto);

        assertThat(responseDto.username()).isEqualTo("tester");
        assertThat(responseDto.tokenType()).isEqualTo("Bearer");
        assertThat(responseDto.accessToken()).isEqualTo("access-token");
        assertThat(responseDto.message()).isEqualTo("로그인이 완료되었습니다.");
    }
}
