package com.toiletnearby.memo.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// MemoContentsVo를 DB의 varchar 컬럼과 매핑한다.
@Converter(autoApply = true)
public class MemoContentsVoConverter implements AttributeConverter<MemoContentsVo, String> {

    // Entity를 DB에 저장할 때
    @Override
    public String convertToDatabaseColumn(MemoContentsVo attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getValue();
    }

    // DB에서 Entity를 꺼내올 때
    @Override
    public MemoContentsVo convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return MemoContentsVo.from(dbData);
    }
}
