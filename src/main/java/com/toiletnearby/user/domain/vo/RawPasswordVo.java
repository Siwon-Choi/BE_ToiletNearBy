package com.toiletnearby.user.domain.vo;

import lombok.Getter;

// 사용자가 입력한 원문 비밀번호다.
// DB에는 이 값을 그대로 저장하면 안 된다.
@Getter
public class RawPasswordVo {

    private static final int MIN_LENGTH = 8;

    private final String value;

    private RawPasswordVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("password는 필수입니다.");
        }

        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("password는 8자 이상이어야 합니다.");
        }

        this.value = value;
    }

    public static RawPasswordVo from(String value) {
        return new RawPasswordVo(value);
    }

}
