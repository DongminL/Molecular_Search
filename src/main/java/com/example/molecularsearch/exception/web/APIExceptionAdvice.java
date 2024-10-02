package com.example.molecularsearch.exception.web;

import com.example.molecularsearch.exception.dto.ErrorDto;
import com.example.molecularsearch.exception.error.CustomException;
import com.example.molecularsearch.exception.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ErrorDto> handleHttpMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("Global Exception, status: {}, error: {}, message: {}, requested url: {}, timestemp: {}",
                e.getStatusCode().value(), ErrorCode.NOT_FOUND_PATH.getError(), ErrorCode.NOT_FOUND_PATH.getMessage(),
                e.getMethod().concat(" ").concat(request.getServletPath()), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorDto.builder()
                        .status(ErrorCode.METHOD_NOT_SUPPORTED.getStatus())
                        .error(ErrorCode.METHOD_NOT_SUPPORTED.getError())
                        .message(ErrorCode.METHOD_NOT_SUPPORTED.getMessage())
                        .build());
    }

    /* 존재하지 않는 자원으로 요청 시, NoResourceFoundException Handling */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDto> handleUnknownResource(NoResourceFoundException e) {
        log.error("Global Exception, status: {}, error: {}, message: {}, requested url: {}, timestemp: {}",
                e.getStatusCode().value(), ErrorCode.NOT_FOUND_PATH.getError(), ErrorCode.NOT_FOUND_PATH.getMessage(),
                e.getHttpMethod().name().concat(" ").concat(e.getResourcePath()), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorDto.builder()
                        .status(ErrorCode.NOT_FOUND_PATH.getStatus())
                        .error(ErrorCode.NOT_FOUND_PATH.getError())
                        .message(ErrorCode.NOT_FOUND_PATH.getMessage())
                        .build());
    }

    /* 존재하지 않는 URL로 API 요청 시, NoHandlerFoundException Handling */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDto> handleUnknownResource(NoHandlerFoundException e) {
        log.error("Global Exception, status: {}, error: {}, message: {}, requested url: {}, timestemp: {}",
                e.getStatusCode().value(), ErrorCode.NOT_FOUND_PATH.getError(), ErrorCode.NOT_FOUND_PATH.getMessage(),
                e.getHttpMethod().concat(" ").concat(e.getRequestURL()), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorDto.builder()
                        .status(ErrorCode.NOT_FOUND_PATH.getStatus())
                        .error(ErrorCode.NOT_FOUND_PATH.getError())
                        .message(ErrorCode.NOT_FOUND_PATH.getMessage())
                        .build());
    }

    /* CustomException 발생 시 Error Handling */
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ErrorDto> exceptionHandler(CustomException e) {
        return toCustomErrorDto(e);
    }

    /* CustomException Error Response 생성 */
    private ResponseEntity<ErrorDto> toCustomErrorDto(CustomException e) {
        log.error("Global Exception, status: {}, error: {}, message: {}, timestemp: {}",
                e.getErrorCode().getStatus(), e.getErrorCode().getError(), e.getMessage(), LocalDateTime.now());

        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ErrorDto.builder()
                        .status(e.getErrorCode().getStatus())
                        .error(e.getErrorCode().getError())
                        .message(e.getMessage())
                        .build());
    }
}
