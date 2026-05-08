package com.toiletnearby.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserLoginResultDtoTest {

    @Test
    @DisplayName("로그인 성공 결과 DTO를 생성한다")
    void createUserLoginResultDto() {
        UserLoginResultDto dto = new UserLoginResultDto("tester", "access-token");

        assertThat(dto.username()).isEqualTo("tester");
        assertThat(dto.accessToken()).isEqualTo("access-token");
    }
}
