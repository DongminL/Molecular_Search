package com.example.molecularsearch.jwt.web;

import com.example.molecularsearch.exception.error.ErrorCode;
import com.example.molecularsearch.jwt.web.dto.JwtDto;
import com.example.molecularsearch.exception.error.CustomException;
import com.example.molecularsearch.jwt.domain.Tokens;
import com.example.molecularsearch.jwt.service.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;  // JWT의 Secret Key로 사용할 값

    @Value("${jwt.expiration-time}")
    private long expiration_time;   // 만료 시간

    private static final String GRANT_TYPE = "Bearer"; // Token Type
    private Key key;    // JWT Key

    private final CustomUserDetailsService customUsersService;

    /* 빈이 생성되고 주입받은 후 Key 생성 */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /* Token 생성 (유저 키 값, 만료 기간) */
    private String createToken(String userPk, Date expiredAt, String role) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("role", role);   // Claims에 권한 역할 추가
        Date now = new Date();  // 현재 시간

        return Jwts.builder()
                .setClaims(claims)  // 유저 정보 저장
                .setIssuedAt(now)   // 토큰 발행 시간
                .setExpiration(expiredAt)   // 만료 기간 설정
                .signWith(key, SignatureAlgorithm.HS512)   // 암호화 알고리즘 설정
                .compact();
    }

    /* Token에서 유저 키 값 가져오기 */
    public String getUserPk(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().toString();
        }
    }

    /* Token에서 유저 권한 값 가져오기 */
    public String getRoleType(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role")
                    .toString();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRATION);
        }
    }

    /* JWT를 생성한 후 JwtDto로 변환 */
    public JwtDto generate(Long userPk, String role) {
        long now = (new Date()).getTime();  // 현재 시간
        Date accessTokenExpiredAt = new Date(now + expiration_time);   // Access Token 만료 기간
        Date refreshTokenExpiredAt = new Date(now + expiration_time * 24L * 7L); // Refresh Token 만료 기간

        String subject = userPk.toString(); // Subject는 유저 키값으로
        String accessToken = createToken(subject, accessTokenExpiredAt, role);    // Access Token 생성
        String refreshToken = createToken(subject, refreshTokenExpiredAt, role);  // Refresh Token 생성

        return JwtDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(GRANT_TYPE)
                .expiredAt(expiration_time)
                .build();
    }

    /* Access Token 재발급 */
    public String reGenerateAccessToken(Long userPk, String role) {
        long now = (new Date()).getTime();  // 현재 시간
        Date accessTokenExpiredAt = new Date(now + expiration_time);   // Access Token 만료 기간

        String subject = userPk.toString(); // Subject는 유저 키값으로

        return createToken(subject, accessTokenExpiredAt, role);
    }

    /* JWT 인증 정보 조회 */
    public Authentication getAuthentication(String token) {
        String userPk = getUserPk(token);
        UserDetails userDetails = customUsersService.loadUserByUsername(userPk);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /* Token의 유효성 검증 */
    public boolean checkToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);  // Token 유효성 검증 및 토큰에서 claims 가져오기

            return true;    // true : 유효
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명, timestemp: {}", LocalDateTime.now());
            throw new CustomException(ErrorCode.TOKEN_INVALIDITY);
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰, timestemp: {}", LocalDateTime.now());
            throw new CustomException(ErrorCode.TOKEN_EXPIRATION);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰, timestemp: {}", LocalDateTime.now());
            throw new CustomException(ErrorCode.TOKEN_INVALIDITY);
        } catch (IllegalArgumentException | DecodingException e) {
            log.error("JWT 토큰이 잘못됨, timestemp: {}", LocalDateTime.now());
            throw new CustomException(ErrorCode.TOKEN_INVALIDITY);
        }
    }

    /* Refresh Token 유효성 검증 */
    public String checkRefreshToken(Tokens tokensObj){
        
        String refreshToken = tokensObj.getRefreshToken();    // refreshToken 값 가져오기

        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken);  // Refresh Token 유효성 검증 및 토큰에서 claims 가져오기

            // Refresh Token의 만료시간이 지나지 않았을 경우, 새로운 Access Token을 생성
            if (!claims.getBody().getExpiration().before(new Date())) {
                return reGenerateAccessToken(Long.parseLong(getUserPk(refreshToken)), getRoleType(refreshToken));
            } else {
                throw new CustomException(ErrorCode.REQUIRE_RELOGIN);
            }
        } catch (Exception e) {
            log.info("잘못된 Refresh Token, timestemp: {}", LocalDateTime.now());
            throw new CustomException(ErrorCode.TOKEN_INVALIDITY);
        }
    }

    /* 남은 만료 시간 가져오기 */
    public Long getExpiration(String token) {

        try {
            // token 남은 유효시간
            Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
            long now = new Date().getTime();

            // token의 현재 남은시간 반환
            return (expiration.getTime() - now);
        } catch (SignatureException e) {
            log.error("JWT 토큰이 잘못됨, timestemp: {}", LocalDateTime.now());
            throw new CustomException(ErrorCode.TOKEN_INVALIDITY);
        }
    }
}
