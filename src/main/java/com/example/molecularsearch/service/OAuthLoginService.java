package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.GoogleUserDto;
import com.example.molecularsearch.dto.JwtDto;
import com.example.molecularsearch.dto.NaverUserDto;
import com.example.molecularsearch.entity.Users;
import com.example.molecularsearch.jwt.JwtProvider;
import com.example.molecularsearch.jwt.Tokens;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final JwtService jwtService;
    private final JwtProvider jwtProvider;
    private final UsersService usersService;

    /* 네이버 로그인 요청 */
    public JwtDto login(NaverUserDto naverUserDto) {
        Users users;

        try {
            users = usersService.searchByUserId(naverUserDto.getUserId());    // 로그인 요청한 계정 정보 가져오기
        } catch (UsernameNotFoundException e) {
            users = usersService.signUp(naverUserDto);    // 회원가입 계정 DB에 저장
        }

        JwtDto jwtDto = jwtProvider.generate(users.getId(), users.getRoleType());   // 토큰 생성
        jwtService.saveToken(jwtDto);   // Redis에 생성한 토큰들 저장

        return jwtDto;
    }

    /* 구글 로그인 요청 */
    public JwtDto login(GoogleUserDto googleUserDto) {
        Users users;

        try {
            users = usersService.searchByUserId(googleUserDto.getUserId());    // 로그인 요청한 계정 정보 가져오기
        } catch (UsernameNotFoundException e) {
            users = usersService.signUp(googleUserDto);    // 회원가입 계정 DB에 저장
        }

        JwtDto jwtDto = jwtProvider.generate(users.getId(), users.getRoleType());   // 토큰 생성
        jwtService.saveToken(jwtDto);   // Redis에 생성한 토큰들 저장

        return jwtDto;
    }

    public Tokens logout(String accessToken) {

        return jwtService.deleteToken(accessToken);
    }
}
