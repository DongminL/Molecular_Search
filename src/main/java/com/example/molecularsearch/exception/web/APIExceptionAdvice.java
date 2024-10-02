package com.example.molecularsearch.exception.web;

import com.example.molecularsearch.exception.dto.ErrorDto;
import com.example.molecularsearch.exception.error.CustomException;
import com.example.molecularsearch.exception.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class APIExceptionAdvice {

    /* 지원하지 않는 Http 메소드로 요청 시, HttpRequestMethodNotSupportedException Handling */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDto> handleHttpMethodNotSupported(HttpServletRequest request) {
        return toCustomErrorDto(ErrorCode.METHOD_NOT_SUPPORTED, request);
    }

    /* 존재하지 않는 URL로 API 요청 시, NoHandlerFoundException Handling */
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorDto> handleUnknownResource(HttpServletRequest request) {
        return toCustomErrorDto(ErrorCode.NOT_FOUND_PATH, request);
    }

    /* CustomException 발생 시 Error Handling */
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ErrorDto> exceptionHandler(CustomException e, HttpServletRequest request) {
        return toCustomErrorDto(e.getErrorCode(), request);
    }

    /* Custom Error Response 생성 */
    private ResponseEntity<ErrorDto> toCustomErrorDto(ErrorCode errorCode, HttpServletRequest request) {
        log.error("Global Exception, status: {}, error: {}, message: {}, requested url: {}, timestemp: {}",
                errorCode.getStatus(), errorCode.getError(), errorCode.getMessage(),
                request.getMethod().concat(" ").concat(request.getServletPath()), LocalDateTime.now());

        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorDto.builder()
                        .path(request.getServletPath())
                        .status(errorCode.getStatus())
                        .error(errorCode.getError())
                        .message(errorCode.getMessage())
                        .build());
    }
}
