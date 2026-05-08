package com.toiletnearby.user.domain.vo;

import lombok.Getter;

// BCrypt로 암호화된 비밀번호다.
@Getter
public class EncodedPasswordVo {

    private final String value;

    private EncodedPasswordVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("encodedPassword는 필수입니다.");
        }

        this.value = value;
    }

    public static EncodedPasswordVo from(String value) {
        return new EncodedPasswordVo(value);
    }

}
