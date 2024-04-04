package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.JwtDto;
import com.example.molecularsearch.jwt.JwtProvider;
import com.example.molecularsearch.jwt.Tokens;
import com.example.molecularsearch.repository.TokensRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProvider jwtProvider;
    private final TokensRepository tokensRepository;

    /* Access Token 값으로 Refresh Token 정보도 가져오기 */
    @Transactional(readOnly = true)
    public Tokens getToken(String accessToken) {
        return tokensRepository.findByAccessToken(accessToken).orElse(null);    // 없으면 null 반환
    }

    /* Access Token 값으로 DB에 존재 여부 확인 */
    @Transactional(readOnly = true)
    public boolean checkToken(String accessToken) {
        return tokensRepository.existsByAccessToken(accessToken);
    }

    /* Token 값들 저장 */
    @Transactional
    public void saveToken(JwtDto jwtDto) {
        // Dto -> Dao
        Tokens tokens = Tokens.builder()
                .accessToken(jwtDto.getAccessToken())
                .refreshToken(jwtDto.getRefreshToken())
                .ttl(jwtProvider.getExpiration(jwtDto.getRefreshToken()))
                .build();

        // Access Token이 이미 존재하면 기존 것 삭제
        if (checkToken(tokens.getAccessToken())) {
            deleteToken(tokens.getAccessToken());
        }

        tokensRepository.save(tokens);   // 토큰 값들 저장
    }

    /* Access Token 값으로 Tokens 삭제 */
    @Transactional
    public Tokens deleteToken(String accessToken) {
        Tokens tokens = getToken(accessToken);  // Tokens 객체 가져오기

        if (tokens == null) {
            return null;
        }

        tokensRepository.deleteById(tokens.getId());    // Tokens Key로 제거

        return tokens;
    }

    /* Access Token만 갱신 */
    @Transactional
    public String reissueAccessToken(String accessToken) {
        // Access Token이 유효하지 않을 때
        if (!jwtProvider.checkToken(accessToken)) {
            Tokens tokens = getToken(accessToken);  // Token 값들 가져오기

            if (tokens == null) {
                throw new RuntimeException("Refresh Token이 만료되었습니다!");
            }

            String newAccessToken = jwtProvider.checkRefreshToken(tokens);  // Access Token 갱신
            log.info("new Access Token : {}", newAccessToken);
            // Redis 값도 갱신
            tokens.updateAccessToken(newAccessToken);
            tokensRepository.save(tokens);

            if (newAccessToken == null) {
                throw new RuntimeException("Refresh Token이 만료되었습니다!");
            }

            return newAccessToken;
        } else {    // 유효하면 null 반환
            return null;
        }
    }

    /* Authorization Header에서 Token 값만 가져오기*/
    public String getHeaderToken(String bearerToken) {
        return bearerToken.substring(7);    // "Bearer " 문자열 제거
    }
}
