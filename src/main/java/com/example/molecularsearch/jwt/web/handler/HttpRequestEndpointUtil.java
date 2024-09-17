package com.example.molecularsearch.jwt.web.handler;

import com.example.molecularsearch.exception.dto.ErrorDto;
import com.example.molecularsearch.exception.error.CustomException;
import com.example.molecularsearch.exception.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HttpRequestEndpointUtil {

    private final DispatcherServlet servlet;

    /* Handling 여부로 Endpoint 존재 판단 */
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

    /* 에러에 따른 Response Body 만들기 */
    ErrorDto makeErrorBody(ErrorCode errorCode, HttpServletRequest request) {
        Optional<ErrorDto> errorBody = Optional.ofNullable(ErrorDto.builder()
                .status(errorCode.getStatus())
                .error(errorCode.getError())
                .message(errorCode.getMessage() + " url: " + request.getRequestURI())
                .build());

        return errorBody.orElseThrow(() -> new CustomException(ErrorCode.SERVER_ERROR));
    }
}
