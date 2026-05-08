package com.toiletnearby.memo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// Controller에서 메모 생성 요청 body를 받는 DTO다
// 작성자 userId는 요청 body가 아니라 JWT 인증 정보에서 가져온다.
public record MemoCreateRequestDto(
        @JsonProperty("toiletid") Long toiletId,
        String contents,
        int good
) {

    // Controller 요청 DTO를 Service 입력 DTO로 변환한다.
    public MemoCreateDto toCreateDto(String authenticatedUserId) {
        return new MemoCreateDto(
                toiletId,
                authenticatedUserId,
                contents,
                good
        );
    }
}
