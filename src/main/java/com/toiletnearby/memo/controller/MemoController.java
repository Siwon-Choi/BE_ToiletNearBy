package com.toiletnearby.memo.controller;

import com.toiletnearby.memo.domain.Memo;
import com.toiletnearby.memo.dto.MemoCreateRequestDto;
import com.toiletnearby.memo.dto.MemoGoodUpdateRequestDto;
import com.toiletnearby.memo.dto.MemoResponseDto;
import com.toiletnearby.memo.dto.MemoUpdateRequestDto;
import com.toiletnearby.memo.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 메모 API를 담당한다.
@RestController
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    // 특정 화장실의 특정 메모를 조회한다.
    @GetMapping("/api/toilet/{TOILET_ID}/memo/{MEMO_ID}")
    public List<MemoResponseDto> getMemo(
            @PathVariable("TOILET_ID") Long toiletId,
            @PathVariable("MEMO_ID") Long memoId
    ) {
        return memoService.getMemo(toiletId, memoId).stream()
                .map(MemoResponseDto::from)
                .toList();
    }

    // 로그인한 사용자가 작성한 메모를 조회한다.
    @GetMapping("/api/toilet/mymemo")
    public List<MemoResponseDto> getMyMemo(Authentication authentication) {
        // 1. Authentication은 Spring Security 인터페이스다.
        // 2. Authentication은 Principal을 상속한다.
        // 3. Principal에 getName()이 정의되어 있다.
        // 4. JWT 인증이 성공하면 Spring Security가 JwtAuthenticationToken을 만든다.
        // 5. JwtAuthenticationToken.getName()은 JWT의 sub 값을 반환한다.
        // 6. 우리 프로젝트에서는 sub에 username을 넣었기 때문에 getName()으로 username이 나온다.
        String userId = getAuthenticatedUserId(authentication);

        return memoService.getMyMemos(userId).stream()
                .map(MemoResponseDto::from)
                .toList();
    }

    // 특정 화장실에 달린 모든 메모를 조회한다.
    @GetMapping("/api/toilet/{TOILET_ID}/memos")
    public List<MemoResponseDto> getMemos(@PathVariable("TOILET_ID") Long toiletId) {
        return memoService.getMemosByToiletId(toiletId).stream()
                .map(MemoResponseDto::from)
                .toList();
    }

    // 메모를 작성한다.
    @PostMapping("/api/toilet/memo")
    public ResponseEntity<MemoResponseDto> createMemo(
            @RequestBody MemoCreateRequestDto requestDto,
            Authentication authentication
    ) {
        String userId = getAuthenticatedUserId(authentication);

        Memo savedMemo = memoService.createMemo(requestDto.toCreateDto(userId));

        return ResponseEntity.ok(MemoResponseDto.from(savedMemo));
    }

    // 본인 메모의 내용을 수정한다.
    @PutMapping("/api/toilet/{TOILET_ID}/memo/{MEMO_ID}")
    public ResponseEntity<Long> updateMemo(
            @PathVariable("TOILET_ID") Long toiletId,
            @PathVariable("MEMO_ID") Long memoId,
            @RequestBody MemoUpdateRequestDto requestDto,
            Authentication authentication
    ) {
        String userId = getAuthenticatedUserId(authentication);

        Long updatedMemoId = memoService.updateMemo(
                requestDto.toUpdateDto(toiletId, memoId, userId)
        );

        return ResponseEntity.ok(updatedMemoId);
    }

    // 메모 평점을 수정한다.
    @PutMapping("/api/toilet/{TOILET_ID}/memo/{MEMO_ID}/good")
    public ResponseEntity<Void> updateGood(
            @PathVariable("TOILET_ID") Long toiletId,
            @PathVariable("MEMO_ID") Long memoId,
            @RequestBody MemoGoodUpdateRequestDto requestDto
    ) {
        memoService.updateGood(requestDto.toGoodUpdateDto(toiletId, memoId));

        return ResponseEntity.ok().build();
    }

    // 본인 메모를 삭제한다.
    @DeleteMapping("/api/toilet/{TOILET_ID}/memo/{MEMO_ID}")
    public ResponseEntity<Long> deleteMemo(
            @PathVariable("TOILET_ID") Long toiletId,
            @PathVariable("MEMO_ID") Long memoId,
            Authentication authentication
    ) {
        String userId = getAuthenticatedUserId(authentication);

        Long deletedMemoId = memoService.deleteMemo(toiletId, memoId, userId);

        return ResponseEntity.ok(deletedMemoId);
    }

    // 인증된 사용자 id를 꺼낸다.
    private String getAuthenticatedUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }

        return authentication.getName();
    }
}
