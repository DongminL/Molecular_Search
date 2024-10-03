package com.example.molecularsearch.jwt.web.handler;

import com.example.molecularsearch.exception.dto.ErrorDto;
import com.example.molecularsearch.exception.error.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    private final HttpRequestEndpointUtil requestEndpointUtil;

    /* 유효한 자격증명을 제공하지 않고 접근하려 할때 401 Unauthorized Error 보냄 */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ErrorDto errorBody;

        if (requestEndpointUtil.isEndpointExist(request)) {
            errorBody = requestEndpointUtil.makeErrorBody(ErrorCode.UNAUTHORIZED, request);
        } else {
            errorBody = requestEndpointUtil.makeErrorBody(ErrorCode.NOT_FOUND_PATH, request);
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);  // 응답 메시지 타입 JSON으로 설정
        response.setStatus(errorBody.getStatus());    // 에러 응답 메시지 Status 설정

        // JSON으로 변환하여 전송
        ObjectMapper json = new ObjectMapper();
        json.writeValue(response.getOutputStream(), errorBody);
    }
}
