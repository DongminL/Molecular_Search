package com.example.molecularsearch.exception.web;

import com.example.molecularsearch.exception.dto.ErrorDto;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    /* 정적 리소스에 잘못된 접근에 대한 에러 핸들링 */
    @GetMapping("/error")
    public ResponseEntity<ErrorDto> handleError() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorDto.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error("NOT_FOUND_PATH")
                        .message("존재하지 않는 경로입니다.")
                        .build());
    }
}
