package com.example.molecularsearch.jwt.web.handler;

import com.example.molecularsearch.exception.dto.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HttpRequestEndpointUtil {

    private final DispatcherServlet servlet;

    /* handler가 존재하는지 판단 */
    boolean isEndpointExist(HttpServletRequest request) {
        for (HandlerMapping handlerMapping : servlet.getHandlerMappings()) {
            try {
                HandlerExecutionChain foundHandler = handlerMapping.getHandler(request);
                if (foundHandler != null) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /* HTTP 상태에 따른 Error Body 만들기 */
    ErrorDto makeErrorBody(HttpStatus status, HttpServletRequest request) {
        Optional<ErrorDto> errorBody = Optional.empty();

        switch (status) {
            case NOT_FOUND -> {
                errorBody = Optional.ofNullable(ErrorDto.builder()
                        .status(status.value())
                        .error(status.name())
                        .message("잘못된 경로입니다. url: " + request.getRequestURI())
                        .build());
            }

            case FORBIDDEN -> {
                errorBody = Optional.ofNullable(ErrorDto.builder()
                        .status(status.value())
                        .error(status.name())
                        .message("접근할 권한이 없습니다. url: " + request.getRequestURI())
                        .build());
            }

            case UNAUTHORIZED -> {
                errorBody = Optional.ofNullable(ErrorDto.builder()
                        .status(status.value())
                        .error(status.name())
                        .message("유효한 인증을 해주세요. url: " + request.getRequestURI())
                        .build());
            }
        }

        return errorBody.orElseThrow(NullPointerException::new);
    }
}
