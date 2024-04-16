package com.example.molecularsearch.api;

import com.example.molecularsearch.jwt.JwtProvider;
import com.example.molecularsearch.service.JwtService;
import com.example.molecularsearch.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsersApiController {

    private final UsersService usersService;
    private final JwtService jwtService;
    private final JwtProvider jwtProvider;

    /* 회원 탈퇴 */
    @DeleteMapping("/api/withdraw")
    public ResponseEntity<?> withdrawUser(@RequestHeader("Authorization") String token) {
        String accessToken = jwtService.getHeaderToken(token);

        if (accessToken == null) {
            return ResponseEntity.badRequest().body("Authorization의 값이 잘못됨");
        }

        Long id = Long.parseLong(jwtProvider.getUserPk(accessToken));   // 토큰에 있는 Users Table의 PK 값 가져오기
        usersService.deleteUser();    // PK로 유저 정보 삭제
        jwtService.deleteToken(accessToken);    // 해당 토큰 정보도 삭제

        return ResponseEntity.ok().body("회원 탈퇴 완료");
    }
}
