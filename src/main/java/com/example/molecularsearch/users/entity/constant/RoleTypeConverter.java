package com.example.molecularsearch.users.entity.constant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RoleTypeConverter implements AttributeConverter<String, Integer> {

    @Override
    public Integer convertToDatabaseColumn(String role) {
        if ("RULE_ADMIN".equals(role)) {
            return 1;
        } else if ("RULE_USER".equals(role)) {
            return 2;
        }
        return null;
    }

    @Override
    public String convertToEntityAttribute(Integer db) {
        if (db == 1) {
            return "RULE_ADMIN";
        } else if (db == 2) {
            return "RULE_USER";
        }
        return null;
    }
}
