package com.toiletnearby.memo.domain.vo;

import lombok.Getter;

// 메모에서 준 평점 값을 표현하는 값 객체
@Getter
public class MemoGoodVo {

    private final int value;

    private MemoGoodVo(int value) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("good은 1부터 5까지 가능합니다.");
        }

        this.value = value;
    }

    // 외부에서는 생성자 대신 from으로 평점 값을 만든다.
    public static MemoGoodVo from(int value) {
        return new MemoGoodVo(value);
    }
}
