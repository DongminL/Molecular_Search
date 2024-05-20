package com.example.molecularsearch.search_log.repository;

import com.example.molecularsearch.search_log.entity.SearchLog;
import com.example.molecularsearch.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    List<SearchLog> findAllByUser_IdOrderByCreatedDate(Long users_id);  // 해당 유저 대한 검색기록 가져오기 (생성일로 정렬)

    void deleteById(Long id);   // SearchLog PK로 검색기록 삭제

    @Modifying
    @Query("delete from SearchLog s where s.user in :user")  // 여러 데이터를 한 번에 삭제 (성능 향상)
    void deleteAllByUser(@Param("user") Users user);    // 해당 유저 대한 모든 기록 삭제

    @Modifying
    @Query("delete from SearchLog s where s.user.id in :users_id")  // 여러 데이터를 한 번에 삭제 (성능 향상)
    void deleteAllByUser_Id(@Param("users_id") Long users_id);    // 해당 유저 대한 모든 기록 삭제
}
