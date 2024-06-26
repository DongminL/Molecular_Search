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

    List<SearchLog> findAllByUser_IdOrderByCreatedDateDesc(Long users_id);  // 해당 유저 대한 검색 기록 가져오기 (생성일로 내림차순 정렬)

    void deleteById(Long id);   // SearchLog PK로 검색 기록 삭제

    @Modifying
    @Query("delete from SearchLog s where s.id in :searchLogList")    // 여러 데이터를 한 번에 삭제 (성능 향상)
    void deleteAllByList(@Param("searchLogList") List<Long> searchLogList);   // 선택된 검색 기록 목록 삭제

    @Modifying
    @Query("delete from SearchLog s where s.user in :user")  // 여러 데이터를 한 번에 삭제 (성능 향상)
    void deleteAllByUser(@Param("user") Users user);    // 해당 유저 대한 모든 기록 삭제

    @Modifying
    @Query("delete from SearchLog s where s.user.id in :users_id")  // 여러 데이터를 한 번에 삭제 (성능 향상)
    void deleteAllByUser_Id(@Param("users_id") Long users_id);    // 해당 유저 대한 모든 기록 삭제
}
