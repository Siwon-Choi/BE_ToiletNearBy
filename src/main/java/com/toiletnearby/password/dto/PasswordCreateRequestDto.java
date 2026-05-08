package com.toiletnearby.password.dto;

// Controller에서 비밀번호 생성 요청 body를 받는 DTO
// userId는 요청 body가 아니라 JWT 인증 정보에서 가져온다.
public record PasswordCreateRequestDto(
        Long toiletId,
        String password
) {

    // Controller 요청 DTO를 Service 입력 DTO로 변환한다.
    public PasswordSaveDto toSaveDto(String authenticatedUserId) {
        return new PasswordSaveDto(toiletId, authenticatedUserId, password);
    }
}
