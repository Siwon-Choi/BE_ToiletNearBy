package com.toiletnearby.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserLoginRequestDtoTest {

    @Test
    @DisplayName("로그인 요청 DTO를 서비스 DTO로 변환한다")
    void convertToServiceDto() {
        UserLoginRequestDto requestDto = new UserLoginRequestDto("tester", "password123");

        UserLoginDto serviceDto = requestDto.toServiceDto();

        assertThat(serviceDto.username()).isEqualTo("tester");
        assertThat(serviceDto.password()).isEqualTo("password123");
    }
}
