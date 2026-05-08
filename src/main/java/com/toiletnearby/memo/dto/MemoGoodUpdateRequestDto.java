package com.toiletnearby.memo.dto;

// Controller에서 메모 평점 수정 요청 body를 받는 DTO
// 어떤 메모를 수정할지는 URL path의 toiletId, memoId로 결정한다.
public record MemoGoodUpdateRequestDto(
        int good
) {

    // Controller 요청 DTO를 Service 입력 DTO로 변환한다.
    public MemoGoodUpdateDto toGoodUpdateDto(Long toiletId, Long memoId) {
        return new MemoGoodUpdateDto(
                toiletId,
                memoId,
                good
        );
    }
}
