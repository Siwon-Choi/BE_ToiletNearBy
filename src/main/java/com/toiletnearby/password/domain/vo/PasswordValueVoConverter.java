package com.toiletnearby.password.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// PasswordValueVo를 DB의 varchar 컬럼과 매핑한다.
@Converter(autoApply = true)
public class PasswordValueVoConverter implements AttributeConverter<PasswordValueVo, String> {

    @Override
    public String convertToDatabaseColumn(PasswordValueVo attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getValue();
    }

    @Override
    public PasswordValueVo convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return PasswordValueVo.from(dbData);
    }
}
