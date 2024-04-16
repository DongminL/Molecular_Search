package com.example.molecularsearch.repository;

import com.example.molecularsearch.entity.SearchLog;
import com.example.molecularsearch.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    List<SearchLog> findAllByUser_IdOrderByCreatedDate(Long users_id);  // 해당 유저 대한 검색기록 가져오기 (생성일로 정렬)

    void deleteById(Long id);   // SearchLog PK로 검색기록 삭제

    void deleteAllByUser(Users user);    // 해당 유저 대한 모든 기록 삭제
}
