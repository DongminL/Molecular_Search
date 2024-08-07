package com.example.molecularsearch.jwt.web.filter;

import com.example.molecularsearch.exception.dto.ErrorDto;
import com.example.molecularsearch.exception.error.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    /* JwtFilter의 에러 처리 */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);    // 다음 filter인 JwtFilter로 값 전달
        } catch (CustomException e) {
            SecurityContextHolder.clearContext();   // Security Context 비우기
            log.debug("Security Context를 비웠습니다.");

            // 에러 응답 메시지 생성
            ErrorDto errorRes = ErrorDto.builder()
                    .status(e.getErrorCode().getStatus())
                    .error(e.getErrorCode().getError())
                    .message(e.getErrorCode().getMessage())
                    .build();
            
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);  // 응답 메시지 타입 JSON으로 설정
            response.setStatus(errorRes.getStatus());    // 에러 응답 메시지 Status 설정

            // JSON으로 변환하여 전송
            ObjectMapper json = new ObjectMapper();
            json.writeValue(response.getOutputStream(), errorRes);
        }
    }
}
