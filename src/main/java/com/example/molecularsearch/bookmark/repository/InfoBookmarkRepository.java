package com.example.molecularsearch.bookmark.repository;

import com.example.molecularsearch.bookmark.entity.InfoBookmark;
import com.example.molecularsearch.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfoBookmarkRepository extends JpaRepository<InfoBookmark, Long> {
    
    List<InfoBookmark> findAllByUser_IdOrderByModifiedDateDesc(Long users_id);    // 해당 유저에 대한 즐겨찾기 리스트 가져오기 (수정일 기준으로 내림차순 정렬)
    
    Boolean existsByUser_IdAndChemInfoId(Long users_id, String chem_id);    // 해당 분자정보의 즐겨찾기 여부 확인
    
    void deleteById(Long id);   // PK 값으로 정보 삭제

    void deleteByUser_IdAndChemInfoId(Long users_id, String chemId);   // ChemInfo ID 값으로 정보 삭제

    @Modifying
    @Query("delete from InfoBookmark b where b.id in :bookmarkList")    // 여러 데이터를 한 번에 삭제 (성능 향상)
    void deleteAllByList(@Param("bookmarkList") List<Long> bookmarkList);   // 선택된 즐겨찾기 목록 삭제

    @Modifying
    @Query("delete from InfoBookmark b where b.user in :user")   // 여러 데이터를 한 번에 삭제 (성능 향상)
    void deleteAllByUser(@Param("user") Users user);   // 해당 유저에 대한 정보 전부 삭제

    @Modifying
    @Query("delete from InfoBookmark b where b.user.id in :users_id")   // 여러 데이터를 한 번에 삭제 (성능 향상)
    void deleteAllByUser_Id(@Param("users_id") Long users_id);  // 해당 유저에 대한 정보 전부 삭제
}
