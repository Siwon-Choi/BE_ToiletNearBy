package com.toiletnearby.user.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


// 원문 password
class RawPasswordVoTest {

    @Test
    @DisplayName("원문 비밀번호를 생성한다")
    void createRawPassword() {
        RawPasswordVo password = RawPasswordVo.from("password123");
        assertThat(password.getValue()).isEqualTo("password123");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("비밀번호는 비어 있을 수 없다")
    void passwordIsRequired(String value) {
        assertThatThrownBy(() -> RawPasswordVo.from(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password는 필수입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567"})
    @DisplayName("비밀번호는 8자 이상이어야 한다")
    void passwordLength(String value) {
        assertThatThrownBy(() -> RawPasswordVo.from(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password는 8자 이상이어야 합니다.");
    }
}
