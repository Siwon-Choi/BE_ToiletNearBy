package com.toiletnearby.memo.dto;

// Controller에서 메모 내용 수정 요청 body를 받는 DTO
// toiletId와 memoId는 URL path에서 받고, userId는 JWT 인증 정보에서 받는다.
public record MemoUpdateRequestDto(
        String contents
) {

    // Controller 요청 DTO를 Service 입력 DTO로 변환한다.
    public MemoUpdateDto toUpdateDto(Long toiletId, Long memoId, String authenticatedUserId) {
        return new MemoUpdateDto(
                toiletId,
                memoId,
                authenticatedUserId,
                contents
        );
    }
}
