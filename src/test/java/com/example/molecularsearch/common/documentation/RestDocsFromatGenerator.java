package com.example.molecularsearch.common.documentation;

import org.springframework.restdocs.snippet.Attributes;

import static org.springframework.restdocs.snippet.Attributes.key;

/* 요청 값의 양식을 설정 */
public interface RestDocsFromatGenerator {

    static Attributes.Attribute phoneNumberFormat() {
        return key("format").value("000-0000-0000");
    }

    static Attributes.Attribute emailFormat() {
        return key("format").value("example-email@example.com");
    }

    static Attributes.Attribute imageUrlFormat() {
        return key("format").value("https://example-image-url.com");
    }

    static Attributes.Attribute tokenFormat() {
        return key("format").value("Bearer YOUR_TOKEN");
    }
}
