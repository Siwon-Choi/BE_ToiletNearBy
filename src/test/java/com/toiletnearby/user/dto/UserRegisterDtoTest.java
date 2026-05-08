package com.toiletnearby.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegisterDtoTest {

    @Test
    @DisplayName("서비스 회원가입 DTO를 생성한다")
    void createUserRegisterDto() {
        UserRegisterDto dto = new UserRegisterDto("tester", "password123");

        assertThat(dto.username()).isEqualTo("tester");
        assertThat(dto.password()).isEqualTo("password123");
    }
}
