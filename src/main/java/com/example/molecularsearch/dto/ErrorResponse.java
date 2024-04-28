package com.example.molecularsearch.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private String timestamp;  // 현재 시간
    private int staus;  // HttpStatus 코드
    private String error;   // HttpStatus 이름
    private String message; // 에러 응답 메시지
}
