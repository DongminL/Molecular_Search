package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.GoogleUserDto;
import com.example.molecularsearch.dto.JwtDto;
import com.example.molecularsearch.dto.NaverUserDto;
import com.example.molecularsearch.entity.Users;
import com.example.molecularsearch.exception.CustomException;
import com.example.molecularsearch.jwt.JwtProvider;
import com.example.molecularsearch.jwt.Tokens;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final JwtService jwtService;
    private final JwtProvider jwtProvider;
    private final UsersService usersService;

    /* 네이버 로그인 요청 */
    public Map<String, String> login(NaverUserDto naverUserDto) {
        Users users;

        try {
            users = usersService.searchByUserId(naverUserDto.getUserId());    // 로그인 요청한 계정 정보 가져오기
        } catch (CustomException e) {
            users = usersService.signUp(naverUserDto);    // 회원가입 계정 DB에 저장
            log.info("네이버 회원가입, user_id: {}, timestemp: {}", naverUserDto.getUserId(), LocalDateTime.now());
        }

        JwtDto jwtDto = jwtProvider.generate(users.getId(), users.getRoleType());   // 토큰 생성
        Map<String, String> token = jwtService.saveToken(jwtDto);   // Redis에 생성한 토큰들 저장

        log.info("네이버 로그인, user_id: {}, timestemp: {}", naverUserDto.getUserId(), LocalDateTime.now());

        return token;
    }

    /* 구글 로그인 요청 */
    public Map<String, String> login(GoogleUserDto googleUserDto) {
        Users users;

        try {
            users = usersService.searchByUserId(googleUserDto.getUserId());    // 로그인 요청한 계정 정보 가져오기
        } catch (UsernameNotFoundException e) {
            users = usersService.signUp(googleUserDto);    // 회원가입 계정 DB에 저장
            log.info("구글 회원가입, user_id : {}, timestemp: {}", googleUserDto.getUserId(), LocalDateTime.now());
        }

        JwtDto jwtDto = jwtProvider.generate(users.getId(), users.getRoleType());   // 토큰 생성
        Map<String, String> token = jwtService.saveToken(jwtDto);   // Redis에 생성한 토큰들 저장

        log.info("구글 로그인, user_id: {}, timestemp: {}", googleUserDto.getUserId(), LocalDateTime.now());

        return token;
    }

    /* 로그아웃 시 토큰 제거 */
    public Tokens logout(String accessToken) {
        String token = jwtService.getHeaderToken(accessToken);  // Access Token 값만 가져오기
        Tokens tokens = jwtService.deleteToken(token);  // 해당 토큰 정보 제거

        log.info("로그아웃, access_token: {}, timestemp: {}", token, LocalDateTime.now());

        return tokens;
    }
}
