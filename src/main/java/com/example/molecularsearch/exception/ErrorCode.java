package com.example.molecularsearch.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* Token Error */
    TOKEN_EXPIRATION(HttpStatus.UNAUTHORIZED, "토큰이 만료 되었습니다."),
    TOKEN_INVALIDITY(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    BAD_REQUEST_HAEDER(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    REQUIRE_RELOGIN(HttpStatus.UNAUTHORIZED, "재로그인이 필요합니다."),

    /* Users Error */
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),

    /* Search Log Error */
    NOT_FOUND_SEARCH_LOG(HttpStatus.NOT_FOUND, "해당 검색 기록을 찾을 수 없습니다."),

    /* Bookmark Error */
    NOT_FOUND_BOOKMARK(HttpStatus.NOT_FOUND, "해당 즐겨찾기를 찾을 수 없습니다."),
    ALREADY_EXIST_BOOKMARK(HttpStatus.CONFLICT, "이미 즐겨찾기한 분자 정보입니다."),

    /* Chem Info Error */
    ALREADY_EXIST_CHEM_INFO(HttpStatus.CONFLICT, "이미 존재하는 분자 정보입니다.");

    private final HttpStatus status;    // HttpStatus 코드
    private final String message;   // 에러 응답 메시지
}
