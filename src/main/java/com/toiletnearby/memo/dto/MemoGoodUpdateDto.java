package com.toiletnearby.memo.dto;

// MemoService에 메모 평점 수정 요청을 전달하는 DTO
public record MemoGoodUpdateDto(
        Long toiletId,
        Long memoId,
        int good
) {
}
