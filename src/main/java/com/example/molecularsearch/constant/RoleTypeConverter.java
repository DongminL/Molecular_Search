package com.example.molecularsearch.constant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RoleTypeConverter implements AttributeConverter<String, Integer> {

    @Override
    public Integer convertToDatabaseColumn(String role) {
        if ("ADMIN".equals(role)) {
            return 1;
        } else if ("USER".equals(role)) {
            return 2;
        }
        return null;
    }

    @Override
    public String convertToEntityAttribute(Integer db) {
        if (db == 1) {
            return "ADMIN";
        } else if (db == 2) {
            return "USER";
        }
        return null;
    }
}
