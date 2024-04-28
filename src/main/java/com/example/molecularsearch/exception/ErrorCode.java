package com.example.molecularsearch.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* Token Error */
    TOKEN_EXPIRATION(HttpStatus.UNAUTHORIZED.value(), "TOKEN_EXPIRATION", "토큰이 만료 되었습니다."),
    TOKEN_INVALIDITY(HttpStatus.UNAUTHORIZED.value(), "TOKEN_INVALIDITY", "유효하지 않은 토큰입니다."),
    BAD_REQUEST_HAEDER(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST_HAEDER",  "잘못된 요청입니다."),
    REQUIRE_RELOGIN(HttpStatus.UNAUTHORIZED.value(), "REQUIRE_RELOGIN", "재로그인이 필요합니다."),

    /* Users Error */
    NOT_FOUND_USER(HttpStatus.NOT_FOUND.value(), "NOT_FOUND_USER", "해당 유저를 찾을 수 없습니다."),
    ALREADY_EXIST_USER(HttpStatus.CONFLICT.value(), "ALREADY_EXIST_USER", "이미 존재하는 유저입니다."),

    /* Search Log Error */
    NOT_FOUND_SEARCH_LOG(HttpStatus.NOT_FOUND.value(), "NOT_FOUND_SEARCH_LOG", "해당 검색 기록을 찾을 수 없습니다."),

    /* Bookmark Error */
    NOT_FOUND_BOOKMARK(HttpStatus.NOT_FOUND.value(), "NOT_FOUND_BOOKMARK", "해당 즐겨찾기를 찾을 수 없습니다."),
    ALREADY_EXIST_BOOKMARK(HttpStatus.CONFLICT.value(), "ALREADY_EXIST_BOOKMARK", "이미 즐겨찾기한 분자 정보입니다."),

    /* Chem Info Error */
    ALREADY_EXIST_CHEM_INFO(HttpStatus.CONFLICT.value(), "ALREADY_EXIST_CHEM_INFO", "이미 존재하는 분자 정보입니다."),
    NOT_FOUND_CHEM_INFO(HttpStatus.NOT_FOUND.value(), "NOT_FOUND_CHEM_INFO", "해당 분자 정보를 찾을 수 없습니다.");

    private final int status;    // HttpStatus 코드
    private final String error; // 에러 이름
    private final String message;   // 에러 응답 메시지
}
