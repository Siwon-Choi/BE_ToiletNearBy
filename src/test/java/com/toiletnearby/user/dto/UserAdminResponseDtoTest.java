package com.toiletnearby.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAdminResponseDtoTest {

    @Test
    @DisplayName("관리자 권한 접근 성공 응답 DTO를 생성한다")
    void createUserAdminResponseDto() {
        UserAdminResponseDto responseDto = UserAdminResponseDto.success();

        assertThat(responseDto.message()).isEqualTo("관리자 권한 접근에 성공했습니다.");
    }
}
