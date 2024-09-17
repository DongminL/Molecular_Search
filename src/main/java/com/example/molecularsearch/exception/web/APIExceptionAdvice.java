package com.example.molecularsearch.exception.web;

import com.example.molecularsearch.exception.dto.ErrorDto;
import com.example.molecularsearch.exception.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class APIExceptionAdvice {

    /* 존재하지 않는 URL로 요청 시, NoHandlerFoundException Handling */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDto> handleUnknownResource(NoHandlerFoundException e) {
        log.error("Global Exception, status: {}, error: {}, message: {}, requested url: {}, timestemp: {}",
                e.getStatusCode().value(), "NOT_FOUND_PATH", "존재하지 않는 경로입니다.",
                e.getHttpMethod().concat(" ").concat(e.getRequestURL()), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorDto.builder()
                        .status(e.getStatusCode().value())
                        .error("NOT_FOUND_PATH")
                        .message("존재하지 않는 경로입니다. path: " + e.getRequestURL())
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
