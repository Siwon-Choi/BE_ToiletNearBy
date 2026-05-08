package com.toiletnearby.user.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// EncodedPasswordVo를 DB의 varchar 컬럼과 매핑한다.
@Converter(autoApply = true)
public class EncodedPasswordVoConverter implements AttributeConverter<EncodedPasswordVo, String> {

    @Override
    public String convertToDatabaseColumn(EncodedPasswordVo attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getValue();
    }

    @Override
    public EncodedPasswordVo convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return EncodedPasswordVo.from(dbData);
    }
}
