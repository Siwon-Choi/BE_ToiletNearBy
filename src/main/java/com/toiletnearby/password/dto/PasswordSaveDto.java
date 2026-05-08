package com.toiletnearby.password.dto;

// PasswordService에 전달할 비밀번호 저장/수정 입력 DTO
public record PasswordSaveDto(
        Long toiletId,
        String userId,
        String password
) {
}
