package com.example.molecularsearch.oauth.web;

import com.example.molecularsearch.jwt.service.JwtService;
import com.example.molecularsearch.oauth.service.OAuthLoginService;
import com.example.molecularsearch.oauth.web.dto.GoogleUserDto;
import com.example.molecularsearch.oauth.web.dto.NaverUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OAuthLoginController {

    private final OAuthLoginService oAuthLoginService;
    private final JwtService jwtService;

    @PostMapping("/api/login/naver")
    public ResponseEntity<?> loginNaver(@RequestBody NaverUserDto naverUserDto) {
        return ResponseEntity.ok(oAuthLoginService.login(naverUserDto));
    }

    @PostMapping("/api/login/google")
    public ResponseEntity<?> loginGoogle(@RequestBody GoogleUserDto googleUserDto) {
        return ResponseEntity.ok(oAuthLoginService.login(googleUserDto));
    }

    /* 만료된 Access Token 갱신 */
    @PatchMapping("/api/login/reissue")
    public ResponseEntity<?> reissueToken(@RequestHeader("Authorization") String token) {
        String accessToken = jwtService.getHeaderToken(token);  // Header 값에서 Bearer 값 제외

        Map<String, String> newAccessToken = jwtService.reissueAccessToken(accessToken);

        return  ResponseEntity.ok(newAccessToken);
    }

    @DeleteMapping("/api/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        oAuthLoginService.logout(token);

        return ResponseEntity.ok("로그아웃 성공");
    }
}
