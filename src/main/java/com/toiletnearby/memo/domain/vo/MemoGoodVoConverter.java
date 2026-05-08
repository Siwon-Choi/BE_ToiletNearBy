package com.toiletnearby.memo.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// MemoGoodVo를 DB의 integer 컬럼과 매핑한다.
@Converter(autoApply = true)
public class MemoGoodVoConverter implements AttributeConverter<MemoGoodVo, Integer> {

    @Override
    public Integer convertToDatabaseColumn(MemoGoodVo attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getValue();
    }

    @Override
    public MemoGoodVo convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }

        return MemoGoodVo.from(dbData);
    }
}
