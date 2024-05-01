package com.example.molecularsearch.exception;

import com.example.molecularsearch.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class APIExceptionAdvice {

    /* CustomException 발생 시 Error Handling */
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ErrorDto> exceptionHandler(CustomException e) {
        return toCustomErrorDto(e);
    }

    /* CustomException Error Response 생성 */
    private ResponseEntity<ErrorDto> toCustomErrorDto(CustomException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ErrorDto.builder()
                        .staus(e.getErrorCode().getStatus())
                        .error(e.getErrorCode().getError())
                        .message(e.getMessage())
                        .build());
    }
}
