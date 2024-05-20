package com.example.molecularsearch.oauth.web;

import com.example.molecularsearch.oauth.web.dto.GoogleUserDto;
import com.example.molecularsearch.oauth.web.dto.NaverUserDto;
import com.example.molecularsearch.jwt.service.JwtService;
import com.example.molecularsearch.oauth.service.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> reissueToken(@RequestHeader("Authorization") String token) {
        String accessToken = jwtService.getHeaderToken(token);  // Header 값에서 Bearer 값 제외

        String newAccessToken = jwtService.reissueAccessToken(accessToken);

        if (newAccessToken != null) {
            return  ResponseEntity.ok(newAccessToken);
        } else {
            return ResponseEntity.badRequest().body("갱신 불필요");
        }
    }

    @DeleteMapping("/api/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        if (oAuthLoginService.logout(token) != null ) {
            return ResponseEntity.ok("로그아웃 성공");
        } else {
            return ResponseEntity.status(406).body("잘못된 토큰 입니다.");
        }
    }
}
