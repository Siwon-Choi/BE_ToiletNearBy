package com.toiletnearby.memo.domain;

import com.toiletnearby.memo.domain.vo.MemoContentsVo;
import com.toiletnearby.memo.domain.vo.MemoContentsVoConverter;
import com.toiletnearby.memo.domain.vo.MemoGoodVo;
import com.toiletnearby.memo.domain.vo.MemoGoodVoConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "memos",
        indexes = {
                @Index(name = "idx_memos_user_id", columnList = "user_id"),
                @Index(name = "idx_memos_toilet_id_memo_id", columnList = "toilet_id,memo_id")
        }
)

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Memo {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "toilet_id", nullable = false)
    @Getter
    private Long toiletId;

    @Column(name = "memo_id", nullable = false)
    @Getter
    private Long memoId;

    @Column(name = "user_id", nullable = false, length = 50)
    @Getter
    private String userId;

    // 메모 내용
    @Column(nullable = false, length = 500)
    @Convert(converter = MemoContentsVoConverter.class)
    private MemoContentsVo contents;

    // 사용자가 준 평점 값이다.
    @Column(nullable = false)
    @Convert(converter = MemoGoodVoConverter.class)
    private MemoGoodVo good;

    private Memo(Long toiletId, Long memoId, String userId, MemoContentsVo contents, MemoGoodVo good) {
        this.toiletId = toiletId;
        this.memoId = memoId;
        this.userId = userId;
        this.contents = contents;
        this.good = good;
    }

    // 메모 생성 규칙
    public static Memo create(
            Long toiletId,
            Long memoId,
            String userId,
            MemoContentsVo contents,
            MemoGoodVo good
    ) {
        // 인자 유효성 검사
        validatePositive("toiletId", toiletId);
        validatePositive("memoId", memoId);
        validateRequiredUserId(userId);
        validateRequiredContents(contents);
        validateRequiredGood(good);

        return new Memo(
                toiletId,
                memoId,
                userId,
                contents,
                good
        );
    }

    // 메모 작성자와 내용을 수정한다.
    public void updateContents(String userId, MemoContentsVo contents) {
        validateRequiredUserId(userId);
        validateRequiredContents(contents);

        this.userId = userId;
        this.contents = contents;
    }

    // 메모 평점 값을 수정한다.
    public void updateGood(MemoGoodVo good) {
        validateRequiredGood(good);

        this.good = good;
    }

    // 문자열 내용만 필요하므로 VO 내부 값을 반환한다.
    public String getContents() {
        return contents.getValue();
    }

    // int 평점만 필요하므로 VO 내부 값을 반환한다.
    public int getGood() {
        return good.getValue();
    }

    private static void validatePositive(String fieldName, Long value) {
        if (value == null || value < 1) {
            throw new IllegalArgumentException(fieldName + "는 1 이상이어야 합니다.");
        }
    }

    private static void validateRequiredUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
    }

    private static void validateRequiredContents(MemoContentsVo contents) {
        if (contents == null) {
            throw new IllegalArgumentException("contents는 필수입니다.");
        }
    }

    private static void validateRequiredGood(MemoGoodVo good) {
        if (good == null) {
            throw new IllegalArgumentException("good은 필수입니다.");
        }
    }
}
