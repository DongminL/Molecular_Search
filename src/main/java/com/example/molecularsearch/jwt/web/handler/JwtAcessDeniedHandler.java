package com.example.molecularsearch.jwt.web.handler;

import com.example.molecularsearch.exception.dto.ErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAcessDeniedHandler extends AccessDeniedHandlerImpl {

    private final HttpRequestEndpointUtil requestEndpointUtil;

    /* 필요한 권한이 없이 접근하려 할때 403 Forbidden Error 보냄 */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        ErrorDto errorBody;

        if (requestEndpointUtil.isEndpointExist(request)) {
            errorBody = requestEndpointUtil.makeErrorBody(HttpStatus.NOT_FOUND, request);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);  // 응답 메시지 타입 JSON으로 설정
            response.setStatus(errorBody.getStatus());    // 에러 응답 메시지 Status 설정

            // JSON으로 변환하여 전송
            ObjectMapper json = new ObjectMapper();
            json.writeValue(response.getOutputStream(), errorBody);
        } else {
            errorBody = requestEndpointUtil.makeErrorBody(HttpStatus.FORBIDDEN, request);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);  // 응답 메시지 타입 JSON으로 설정
            response.setStatus(errorBody.getStatus());    // 에러 응답 메시지 Status 설정

            // JSON으로 변환하여 전송
            ObjectMapper json = new ObjectMapper();
            json.writeValue(response.getOutputStream(), errorBody);
        }
    }
}
