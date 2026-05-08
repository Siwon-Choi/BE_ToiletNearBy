package com.toiletnearby.memo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toiletnearby.memo.domain.Memo;

// 메모 조회 응답 DTO
public record MemoResponseDto(
        Long id,
        @JsonProperty("toiletid") Long toiletId,
        @JsonProperty("memoid") Long memoId,
        @JsonProperty("userid") String userId,
        String contents,
        int good
) {

    // Memo 엔티티를 응답 DTO로 변환한다.
    public static MemoResponseDto from(Memo memo) {
        return new MemoResponseDto(
                memo.getId(),
                memo.getToiletId(),
                memo.getMemoId(),
                memo.getUserId(),
                memo.getContents(),
                memo.getGood()
        );
    }
}
