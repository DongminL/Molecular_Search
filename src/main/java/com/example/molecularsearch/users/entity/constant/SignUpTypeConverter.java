package com.example.molecularsearch.users.entity.constant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SignUpTypeConverter implements AttributeConverter<String, Integer> {

    /* Entity에서 DB로 저장될 때 */
    @Override
    public Integer convertToDatabaseColumn(String type) {
        if ("NAVER".equals(type)) {
            return 1;
        } else if ("GOOGLE".equals(type)) {
            return 2;
        }
        return null;
    }

    /* DB에서 Entity로 받을 때 */
    @Override
    public String convertToEntityAttribute(Integer db) {
        if (db == 1) {
            return "NAVER";
        } else if (db == 2) {
            return "GOOGLE";
        }
        return null;
    }
}
