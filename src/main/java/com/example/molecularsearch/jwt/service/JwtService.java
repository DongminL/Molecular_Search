package com.example.molecularsearch.jwt.service;

import com.example.molecularsearch.jwt.web.dto.JwtDto;
import com.example.molecularsearch.exception.error.CustomException;
import com.example.molecularsearch.exception.error.ErrorCode;
import com.example.molecularsearch.jwt.web.JwtProvider;
import com.example.molecularsearch.jwt.domain.Tokens;
import com.example.molecularsearch.jwt.repository.TokensRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProvider jwtProvider;
    private final TokensRepository tokensRepository;

    /* Access Token 값으로 Refresh Token 정보도 가져오기 */
    public Tokens getToken(String accessToken) {
        return tokensRepository.findByAccessToken(accessToken).orElseThrow(() ->
                new CustomException(ErrorCode.REQUIRE_RELOGIN));    // 없으면 재로그인 하라는 에러 발생
    }

    /* Access Token 값으로 DB에 존재 여부 확인 */
    public boolean checkToken(String accessToken) {
        return tokensRepository.existsByAccessToken(accessToken);
    }

    /* Token 값들 저장 */
    public Map<String, String> saveToken(JwtDto jwtDto) {
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

        // Client에게 전달할 값
        Map<String, String> tokenInfo = new HashMap<>();
        tokenInfo.put("accessToken", tokens.getAccessToken());
        tokenInfo.put("grantType", jwtDto.getGrantType());
        tokenInfo.put("expiredAt", jwtDto.getExpiredAt().toString());

        log.debug("Token 저장, timestemp: {}", LocalDateTime.now());

        return tokenInfo;
    }

    /* Access Token 값으로 Tokens 삭제 */
    public void deleteToken(String accessToken) {
        Tokens tokens = getToken(accessToken);  // Tokens 객체 가져오기

        tokensRepository.deleteById(tokens.getId());    // Tokens Key로 제거

        log.debug("Token 삭제, timestemp: {}", LocalDateTime.now());
    }

    /* Access Token만 갱신 */
    public Map<String, String> reissueAccessToken(String accessToken) {
        // Access Token이 유효할 때
        if (jwtProvider.checkToken(accessToken)) {
            Tokens tokens = getToken(accessToken);  // Token 값들 가져오기

            if (tokens == null) {
                throw new CustomException(ErrorCode.REQUIRE_RELOGIN);
            }

            String newAccessToken = jwtProvider.checkRefreshToken(tokens);  // Access Token 갱신
            // Redis 값도 갱신
            tokens.updateAccessToken(newAccessToken);
            tokensRepository.save(tokens);

            if (newAccessToken == null) {
                throw new CustomException(ErrorCode.REQUIRE_RELOGIN);
            }

            // Client에게 전달할 값
            Map<String, String> tokenInfo = new HashMap<>();
            tokenInfo.put("accessToken", newAccessToken);
            tokenInfo.put("grantType", "Bearer");
            tokenInfo.put("expiredAt", jwtProvider.getExpiration(newAccessToken).toString());

            log.debug("Access Token 갱신, timestemp: {}", LocalDateTime.now());

            return tokenInfo;
        } else {    // 유효하지 않으면
            throw new CustomException(ErrorCode.REQUIRE_RELOGIN);
        }
    }

    /* Authorization Header에서 Token 값만 가져오기*/
    public String getHeaderToken(String bearerToken) {
        // 유효한 헤더 값인지 검증
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);    // "Bearer " 부분을 자르고 JWT만 가져옴
        }

        throw new CustomException(ErrorCode.BAD_REQUEST);
    }
}
