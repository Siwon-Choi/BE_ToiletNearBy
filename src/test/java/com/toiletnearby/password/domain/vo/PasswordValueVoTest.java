package com.toiletnearby.password.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PasswordValueVoTest {

    @Test
    @DisplayName("비밀번호 값을 만든다")
    void createPasswordValue() {
        PasswordValueVo vo = PasswordValueVo.from("1234");

        assertThat(vo.getValue()).isEqualTo("1234");
    }

    @Test
    @DisplayName("비밀번호는 비어 있을 수 없다")
    void passwordIsRequired() {
        assertThatThrownBy(() -> PasswordValueVo.from(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password는 필수입니다.");
    }
}
