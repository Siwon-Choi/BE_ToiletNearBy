package com.toiletnearby.user.domain.vo;

import lombok.Getter;

// username 값 자체의 규칙을 가진 VO
@Getter
public class UsernameVo {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 20;

    private final String value;

    private UsernameVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("username은 필수입니다.");
        }

        String trimmedValue = value.trim();

        if (trimmedValue.length() < MIN_LENGTH || trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("username은 3자 이상 20자 이하여야 합니다.");
        }

        this.value = trimmedValue;
    }

    // UsernameVo VO를 생성  
    public static UsernameVo from(String value) {
        return new UsernameVo(value);
    }

}
