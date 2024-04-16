package com.example.molecularsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtDto {

    private String accessToken; // 접근 토큰
    private String refreshToken;    // 갱신 토큰
    private String grantType;   // 토큰 타입
    private Long expiredAt; // 만료 시간 (ms)
}
