package com.toiletnearby.memo.dto;

// MemoService에 메모 내용 수정 요청을 전달하는 DTO
public record MemoUpdateDto(
        Long toiletId,
        Long memoId,
        String userId,
        String contents
) {
}
