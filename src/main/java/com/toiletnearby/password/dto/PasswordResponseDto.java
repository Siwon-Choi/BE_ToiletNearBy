package com.toiletnearby.password.dto;

import com.toiletnearby.password.domain.Password;

// 비밀번호 저장/수정 결과 응답 DTO
public record PasswordResponseDto(
        Long id,
        Long toiletId,
        String userId,
        String password
) {

    // Password 엔티티를 응답 DTO로 변환한다.
    public static PasswordResponseDto from(Password password) {
        return new PasswordResponseDto(
                password.getId(),
                password.getToiletId(),
                password.getUserId(),
                password.getPassword()
        );
    }
}
