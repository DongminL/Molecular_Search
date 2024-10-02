package com.example.molecularsearch.exception.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorDto {

    private final String timestamp = LocalDateTime.now().toString();  // 현재 시간
    private final String path;    // 요청한 URL 경로
    private final int status;  // HttpStatus 코드
    private final String error;   // HttpStatus 이름
    private final String message; // 에러 응답 메시지

    @Builder
    public ErrorDto(String path, int status, String error, String message) {
        this.path = path;
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
