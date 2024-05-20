package com.example.molecularsearch.jwt.repository;

import com.example.molecularsearch.jwt.domain.Tokens;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokensRepository extends CrudRepository<Tokens, Long> {

    boolean existsByAccessToken(String accessToken);    // DB에 해당 Access Token 값이 있는지 검사

    Optional<Tokens> findByAccessToken(String accessToken);   // Access Token 값으로 Refresh Token 정보 불러오기

    void deleteById(Long id);   // Token Key 값으로 Tokens 값 삭제
}
