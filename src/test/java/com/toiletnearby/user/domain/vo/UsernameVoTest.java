package com.toiletnearby.user.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


// UsernameVo 규칙 테스트
class UsernameVoTest {

    @Test
    @DisplayName("username 값을 생성한다")
    void createUsername() {
        UsernameVo username = UsernameVo.from("tester");

        assertThat(username.getValue()).isEqualTo("tester");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("username은 비어 있을 수 없다")
    void usernameIsRequired(String value) {
        assertThatThrownBy(() -> UsernameVo.from(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("username은 필수입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "abcdefghijklmnopqrstu"})
    @DisplayName("username은 3자 이상 20자 이하여야 한다")
    void usernameLength(String value) {
        assertThatThrownBy(() -> UsernameVo.from(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("username은 3자 이상 20자 이하여야 합니다.");
    }
}
