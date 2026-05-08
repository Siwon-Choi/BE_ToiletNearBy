package com.toiletnearby.password.domain.vo;

import lombok.Getter;

// 화장실 비밀번호 값 자체를 표현하는 VO다.
@Getter
public class PasswordValueVo {

    private final String value;

    private PasswordValueVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("password는 필수입니다.");
        }

        if (value.length() > 255) {
            throw new IllegalArgumentException("password는 255자 이하여야 합니다.");
        }

        this.value = value;
    }

    // 외부 문자열을 검증된 비밀번호 값으로 바꾼다.
    public static PasswordValueVo from(String value) {
        return new PasswordValueVo(value);
    }
}
