package com.example.molecularsearch.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider implements InitializingBean {

    private Key key;
    private final String secret;
    private final long expiration_time;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiration-time}") long expiration_time) {
        this.secret = secret;
        this.expiration_time = expiration_time;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /* Token 생성 (유저 키 값, 만료 기간) */
    public String createToken(String subject, Date expiredAt) {
        return Jwts.builder()
                .setSubject(subject)    // 유저 키값 명시
                .setExpiration(expiredAt)   // 만료 기간 설정
                .signWith(this.key, SignatureAlgorithm.HS512)   // 암호화 알고리즘 설정
                .compact();
    }
}
