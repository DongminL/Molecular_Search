package com.example.molecularsearch.jwt;

import com.example.molecularsearch.dto.ErrorResponse;
import com.example.molecularsearch.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    /* JwtFilter의 에러 처리 */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);    // 다음 filter인 JwtFilter로 값 전달
        } catch (CustomException e) {
            // 에러 응답 메시지 생성
            ErrorResponse errorRes = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .staus(e.getErrorCode().getStatus().value())
                    .error(e.getErrorCode().getStatus().name())
                    .message(e.getErrorCode().getMessage())
                    .build();
            
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);  // 응답 메시지 타입 JSON으로 설정
            response.setStatus(errorRes.getStaus());    // 에러 응답 메시지 Status 설정

            // JSON으로 변환하여 전송
            ObjectMapper json = new ObjectMapper();
            json.writeValue(response.getOutputStream(), errorRes);
        }
    }
}
