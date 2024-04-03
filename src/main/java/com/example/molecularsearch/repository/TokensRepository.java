package com.example.molecularsearch.repository;

import com.example.molecularsearch.jwt.Tokens;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokensRepository extends CrudRepository<Tokens, Long> {

    boolean existsByAccessToken(String accessToken);    // DB에 해당 Access Token 값이 있는지 검사

    Optional<Tokens> findByAccessToken(String accessToken);   // Access Token 값으로 Refresh Token 정보 불러오기

    Optional<Tokens> deleteByAccessToken(String accessToken);   // Access Token 값으로 Tokens 값 삭제
}
