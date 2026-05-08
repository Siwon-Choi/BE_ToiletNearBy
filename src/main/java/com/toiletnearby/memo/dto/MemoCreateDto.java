package com.toiletnearby.memo.dto;

// MemoService에 메모 생성 요청을 전달하는 DTO
public record MemoCreateDto(
        Long toiletId,
        String userId,
        String contents,
        int good
) {
}
