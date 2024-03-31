package com.example.molecularsearch.service;

import com.example.molecularsearch.dto.JwtDto;
import com.example.molecularsearch.jwt.JwtProvider;
import com.example.molecularsearch.jwt.Tokens;
import com.example.molecularsearch.repository.TokensRepository;
import jakarta.persistence.EntityNotFoundException;
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
        return tokensRepository.findByAccessToken(accessToken).orElseThrow(() ->
                new EntityNotFoundException("존재하지 않은 토큰입니다!"));
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
    public void deleteToken(String accessToken) {
        tokensRepository.deleteByAccessToken(accessToken);
    }

    /* Access Token만 갱신 */
    @Transactional(readOnly = true)
    public String reissueAccessToken(String accessToken) {
        Tokens tokens = getToken(accessToken);  // Token 값들 가져오기

        if (tokens == null) {
            throw new RuntimeException("Refresh Token이 만료되었습니다!");
        }

        String newAccessToken = jwtProvider.checkRefreshToken(tokens);  // Access Token 갱신
        // Redis 값도 갱신
        tokens.updateAccessToken(newAccessToken);
        tokensRepository.save(tokens);

        if (newAccessToken == null) {
            throw new RuntimeException("Refresh Token이 만료되었습니다!");
        }

        return newAccessToken;
    }
}
