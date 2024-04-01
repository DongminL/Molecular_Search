package com.example.molecularsearch.api;

import com.example.molecularsearch.dto.GoogleUserDto;
import com.example.molecularsearch.dto.NaverUserDto;
import com.example.molecularsearch.service.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsersApiController {

    private final OAuthLoginService oAuthLoginService;

    @PostMapping("/api/login/naver")
    public ResponseEntity<?> loginNaver(@RequestBody NaverUserDto naverUserDto) {
        return ResponseEntity.ok(oAuthLoginService.login(naverUserDto));
    }

    @PostMapping("/api/login/google")
    public ResponseEntity<?> loginGoogle(@RequestBody GoogleUserDto googleUserDto) {
        return ResponseEntity.ok(oAuthLoginService.login(googleUserDto));
    }
}
