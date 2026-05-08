package com.toiletnearby.memo.domain.vo;

import lombok.Getter;

// 메모 내용을 표현하는 값 객체
@Getter
public class MemoContentsVo {

    private static final int MAX_LENGTH = 500;

    private final String value;

    private MemoContentsVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("contents는 필수입니다.");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("contents는 500자 이하입니다.");
        }

        this.value = value;
    }

    // 외부에서는 생성자 대신 from으로 메모 내용을 만든다. -> Static Factory Method
    public static MemoContentsVo from(String value) {
        return new MemoContentsVo(value);
    }
}
