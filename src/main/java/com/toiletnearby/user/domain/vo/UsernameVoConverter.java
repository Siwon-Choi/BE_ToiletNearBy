package com.toiletnearby.user.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// UsernameVo를 DB의 varchar 컬럼과 매핑한다.
@Converter(autoApply = true)
public class UsernameVoConverter implements AttributeConverter<UsernameVo, String> {

    @Override
    public String convertToDatabaseColumn(UsernameVo attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getValue();
    }

    @Override
    public UsernameVo convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return UsernameVo.from(dbData);
    }
}
