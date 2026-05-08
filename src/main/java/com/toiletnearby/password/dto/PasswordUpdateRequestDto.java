package com.toiletnearby.password.dto;

// Controller에서 비밀번호 수정 요청 body를 받는 DTO다.
// 어떤 화장실 비밀번호를 수정할지는 URL path의 toiletId로 결정한다.
public record PasswordUpdateRequestDto(
        String password
) {

    // Controller 요청 DTO를 Service 입력 DTO로 변환한다.
    public PasswordSaveDto toSaveDto(Long toiletId, String authenticatedUserId) {
        return new PasswordSaveDto(toiletId, authenticatedUserId, password);
    }
}
