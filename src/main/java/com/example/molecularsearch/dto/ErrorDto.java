package com.example.molecularsearch.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorDto {

    private final String timestamp = LocalDateTime.now().toString();  // 현재 시간
    private final int staus;  // HttpStatus 코드
    private final String error;   // HttpStatus 이름
    private final String message; // 에러 응답 메시지

    @Builder
    public ErrorDto(int staus, String error, String message) {
        this.staus = staus;
        this.error = error;
        this.message = message;
    }
}
