package com.example.molecularsearch.users.repository;

import com.example.molecularsearch.users.entity.Users;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    boolean existsByUserId(String userId);  // DB에 해당 UserId 값이 있는지 검사

    Optional<Users> findByUserId(String userId);  // userId 값으로 User 정보 불러오기

    Optional<Users> findById(@NonNull Long id);  // Users Table의 PK 값으로 User 정보 불러오기

    void deleteById(Long id);

    @Modifying
    @Query("UPDATE Users u SET u.nickname = :#{#user.nickname} WHERE u.id = :#{#user.id}")  // Select 쿼리 사용되지 않음
    void updateNickname(@Param("user") Users user); // 해당 유저의 닉네임 변경
}
