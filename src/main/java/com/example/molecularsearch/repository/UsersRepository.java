package com.example.molecularsearch.repository;

import com.example.molecularsearch.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    boolean existsByUserId(String userId);  // DB에 해당 UserId 값이 있는지 검사

    Users findByUserId(String userId);  // userId 값으로 DB User 정보 불러오기
}
